package lolpago.spell.application.result;

public record SpellCheckResult(
	Long summonerId,
	String championName,
	String spellName,
	Long spellCoolTime,
	Long skillAbilityHaste,
	String type
) {
}
