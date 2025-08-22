package lolpago.spell.application;

import static lolpago.common.exception.ExceptionMessage.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import lolpago.staticdata.domain.repository.RuneRepository;
import lolpago.staticdata.domain.repository.SpellRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lolpago.client.riot.RiotClient;
import lolpago.common.exception.type.NotFoundException;
import lolpago.spell.application.command.SpellCheckCommand;
import lolpago.spell.application.response.SpectatorCurrentGameInfoApiResponse;
import lolpago.spell.application.result.SpellCheckResult;
import lolpago.staticdata.domain.Champion;
import lolpago.staticdata.domain.repository.ChampionRepository;
import lolpago.summoner.domain.Summoner;
import lolpago.summoner.domain.SummonerRepository;
import lombok.extern.slf4j.Slf4j;

/**
 * 스펠 체크 및 쿨타임 확인 로직을 처리하는 서비스 클래스
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SpellCheckService {
	// 스펠 이름과 각 스펠의 쿨타임을 매칭
	private static final Map<String, Long> SPELL_COOL_TIME = Map.of(
			"점멸", 300L, // 300_000L : 5분
			"순간이동", 360L,
			"점화", 180L,
			"회복", 240L,
			"탈진", 210L,
			"정화", 210L,
			"방어막", 180L,
			"유체화", 210L,
			"강타", 90L
	);

	// 스펠 목록
	private static final List<String> spells = List.of(
			"점멸", "순간이동", "점화", "회복", "탈진", "정화", "방어막", "유체화", "강타"
	);

	private static final Map<Long, Long> coolTimeRunes = Map.of(
			8347L, 18L, // 우주적 통찰력 스킬 가속 +18
			5007L, 8L // 보조룬 스킬 가속 +8
	);

	private final RiotClient riotClient;
	private final SummonerRepository summonerRepository;
	private final ChampionRepository championRepository;
	private final SpellRepository spellRepository;

	/**
	 * 주어진 텍스트로부터 적 챔피언 이름과 스펠명을 추출
	 * 해당 스펠의 쿨타임을 Redis 에 등록
	 */
	public SpellCheckResult championSpellCheck(SpellCheckCommand command) {
		// 소환사 정보 조회
		Summoner summoner = summonerRepository.getById(command.summonerId());

		// 현재 게임 정보 Riot API 호출
		ResponseEntity<SpectatorCurrentGameInfoApiResponse> spectatorCurrentGameInfoApiResponse =
		riotClient.getSpectatorCurrentGameInfo(summoner.getPuuid(), command.region());
		// API 실패 또는 응답 없음
		if(!spectatorCurrentGameInfoApiResponse.getStatusCode().is2xxSuccessful() ||
			Objects.isNull(spectatorCurrentGameInfoApiResponse.getBody())) {
			throw new NotFoundException(SPECTATOR_CURRENT_GAME_INFO_NOT_FOUND_MESSAGE);
		}

		SpectatorCurrentGameInfoApiResponse gameInfo = spectatorCurrentGameInfoApiResponse.getBody();

		// 자신의 팀 ID 찾기 (ID를 통해 상대 팀의 ID 알 수 있다)
		Long myTeamId = gameInfo.participants().stream()
			.filter(p -> summoner.getPuuid().equals(p.puuid()))
			.map(SpectatorCurrentGameInfoApiResponse.CurrentGameParticipant::teamId)
				.filter(Objects::nonNull)
			.findFirst()
			.orElseThrow(() -> new NotFoundException(MY_TEAM_ID_NOT_FOUND_MESSAGE));

		// 상대 챔피언들의 한글 이름 목록
		List<String> enemyChampions = gameInfo.participants().stream()
			.filter(p -> !p.teamId().equals(myTeamId))
			.map(SpectatorCurrentGameInfoApiResponse.CurrentGameParticipant::championId)
			.map(championId -> championRepository.getById(championId.intValue()))
			.map(Champion::getKrName)
			.toList();

		// finalText 에 적 챔피언 이름이 없으면 예외
		if(!isChampion(command.finalText(), enemyChampions)) {
			throw new NotFoundException(CHAMPION_NAME_NOT_FOUND_IN_VOICE_MESSAGE);
		}

		// finalText 에 유효한 스펠 이름이 없으면 예외
		if(!isSpell(command.finalText())) {
			throw new NotFoundException(SPELL_NAME_NOT_FOUND_IN_VOICE_MESSAGE);
		}

		// 현재 게임 상대팀 목록
		List<SpectatorCurrentGameInfoApiResponse.CurrentGameParticipant> enemyParticipants =
				gameInfo.participants().stream()
						.filter(p -> !p.teamId().equals(myTeamId))
						.toList();

		// 상대 챔피언들의 챔피언 ID 목록
		List<Long> enemyChampionIds = gameInfo.participants().stream()
				.filter(p -> !p.teamId().equals(myTeamId))
				.map(SpectatorCurrentGameInfoApiResponse.CurrentGameParticipant::championId)
				.filter(Objects::nonNull)
				.toList();

		// 텍스트에서 챔피언 이름과 스펠명 추출
		String championName = extractChampionName(command.finalText(), enemyChampions);
		Long championId = extractChampionId(championName, enemyChampionIds);
		String spellName = extractSpell(command.finalText());

		// 상대 유저 추출
		SpectatorCurrentGameInfoApiResponse.CurrentGameParticipant enemyParticipant =
				extractParticipant(enemyParticipants, championId);

		// 상대 챔피언이 해당 스펠을 들고 있는지 확인
		if (!hasSpell(enemyParticipant, spellName)) {
			throw new NotFoundException(SPELL_NOT_FOUND_MESSAGE.format(spellName));
		}

		// 룬 정보를 통한 상대 스킬 가속 계산
		Long skillAbilityHaste = getSkillAbilityHaste(enemyParticipant);

		return new SpellCheckResult(
			summoner.getId(), championName, spellName, getSpellCoolTime(spellName), skillAbilityHaste, "spell"
		);
	}

	// 텍스트에 포함된 챔피언 이름이 적 챔피언 중에 있는지 확인
	private boolean isChampion(String finalText, List<String> enemyChampions) {
		return enemyChampions.stream()
			.anyMatch(finalText::contains);
	}

	// 텍스트에 포함된 스펠 이름이 유효한 스펠 목록에 있는지 확인
	private boolean isSpell(String finalText) {
		return spells.stream()
			.anyMatch(finalText::contains);
	}

	// 스펠 이름에 해당하는 쿨타임 반환
	private long getSpellCoolTime(String spellName) {
		return SPELL_COOL_TIME.get(spellName);
	}

	// 텍스트에서 적 챔피언 이름 추출
	private String extractChampionName(String finalText, List<String> enemyChampions) {
		return enemyChampions.stream()
			.filter(finalText::contains)
			.findFirst().orElseThrow(()-> new NotFoundException(CHAMPION_NAME_NOT_FOUND_IN_VOICE_MESSAGE));
	}

	// 적 챔피언 ID 추출
	private Long extractChampionId(String championName, List<Long> enemyChampionIds) {
		return enemyChampionIds.stream()
				.filter(id -> championRepository.getById(id.intValue()).getKrName().equals(championName))
				.findFirst()
				.orElseThrow(() -> new NotFoundException(CHAMPION_NOT_FOUND_MESSAGE.format(championName)));
	}

	// 텍스트에서 스펠 이름 추출
	private String extractSpell(String finalText) {
		return spells.stream()
			.filter(finalText::contains)
			.findFirst().orElseThrow(()-> new NotFoundException(SPELL_NAME_NOT_FOUND_IN_VOICE_MESSAGE));
	}

	// 상대 챔피언에 해당하는 유저 추출
	private SpectatorCurrentGameInfoApiResponse.CurrentGameParticipant extractParticipant(
			List<SpectatorCurrentGameInfoApiResponse.CurrentGameParticipant> enemyParticipants, Long championId
	) {
		for (SpectatorCurrentGameInfoApiResponse.CurrentGameParticipant participant : enemyParticipants) {
			if (Objects.equals(championId, participant.championId())) {
				return participant;
			}
		}
		throw new NotFoundException(SPECTATOR_CURRENT_GAME_INFO_NOT_FOUND_PARTICIPANT);
	}

	// 해당 챔피언이 해당 스펠을 가지고 있는지 확인
	private boolean hasSpell(SpectatorCurrentGameInfoApiResponse.CurrentGameParticipant enemyParticipant, String spellName) {
		String spellD = spellRepository.getById(enemyParticipant.spell1Id().intValue()).getName();
		String spellF = spellRepository.getById(enemyParticipant.spell2Id().intValue()).getName();

        return spellD.equals(spellName) || spellF.equals(spellName);
    }

	// 상대의 룬 정보를 통해 스킬 가속 계산
	private Long getSkillAbilityHaste(SpectatorCurrentGameInfoApiResponse.CurrentGameParticipant enemyParticipant) {
		long totalAbilityHaste = 0L;

		List<Long> perkIds = enemyParticipant.perks().perkIds();
		for (Long perkId : perkIds) {
			totalAbilityHaste += coolTimeRunes.getOrDefault(perkId, 0L);
		}

		return totalAbilityHaste;
	}

}
