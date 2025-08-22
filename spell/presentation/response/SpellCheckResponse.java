package lolpago.spell.presentation.response;

import lolpago.spell.application.result.SpellCheckResult;

public record SpellCheckResponse(
	Long summonerId,
	String championName,
	String spellName,
	Long spellCoolTime,
	Long skillAbilityHaste,
	String type
) {
	public static SpellCheckResponse from(
		SpellCheckResult spellCheckResult
	) {
		return new SpellCheckResponse(spellCheckResult.summonerId(), spellCheckResult.championName(),
			spellCheckResult.spellName(), spellCheckResult.spellCoolTime(), spellCheckResult.skillAbilityHaste(), spellCheckResult.type());
	}
}
