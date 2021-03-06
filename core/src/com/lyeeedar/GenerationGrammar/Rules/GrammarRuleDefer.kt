package com.lyeeedar.GenerationGrammar.Rules

import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.ObjectFloatMap
import com.badlogic.gdx.utils.ObjectMap
import com.badlogic.gdx.utils.XmlReader
import com.lyeeedar.GenerationGrammar.Area
import com.lyeeedar.GenerationGrammar.GrammarSymbol
import ktx.collections.set

class GrammarRuleDefer : AbstractGrammarRule()
{
	lateinit var rule: String

	suspend override fun execute(area: Area, ruleTable: ObjectMap<String, AbstractGrammarRule>, defines: ObjectMap<String, String>, variables: ObjectFloatMap<String>, symbolTable: ObjectMap<Char, GrammarSymbol>, seed: Long, deferredRules: Array<DeferredRule>)
	{
		val rule = ruleTable[rule]

		synchronized(deferredRules)
		{
			deferredRules.add(DeferredRule(rule, area, defines, variables, symbolTable, seed))
		}
	}

	override fun parse(xml: XmlReader.Element)
	{
		rule = xml.get("Rule")
	}
}

data class DeferredRule(val rule: AbstractGrammarRule, val area: Area, val defines: ObjectMap<String, String>, val variables: ObjectFloatMap<String>, val symbolTable: ObjectMap<Char, GrammarSymbol>, val seed: Long)
{
	init
	{
		if (!areaMap.containsKey(area))
		{
			areaMap[area] = area.copy()
		}
		if (!defineMap.containsKey(defines))
		{
			val newDefines = ObjectMap<String, String>()
			newDefines.putAll(defines)

			defineMap[defines] = newDefines
		}
		if (!variableMap.containsKey(variables))
		{
			val newVariables = ObjectFloatMap<String>()
			newVariables.putAll(variables)

			variableMap[variables] = newVariables
		}
		if (!symbolMap.containsKey(symbolTable))
		{
			val newSymbols = ObjectMap<Char, GrammarSymbol>()
			symbolTable.forEach { newSymbols.put(it.key, it.value.copy()) }

			symbolMap[symbolTable] = newSymbols
		}
	}

	suspend fun execute(ruleTable: ObjectMap<String, AbstractGrammarRule>, deferredRules: Array<DeferredRule>)
	{
		rule.execute(areaMap[area], ruleTable, defineMap[defines], variableMap[variables], symbolMap[symbolTable], seed, deferredRules)
	}

	companion object
	{
		val areaMap = ObjectMap<Area, Area>()
		val defineMap = ObjectMap<ObjectMap<String, String>, ObjectMap<String, String>>()
		val variableMap = ObjectMap<ObjectFloatMap<String>, ObjectFloatMap<String>>()
		val symbolMap = ObjectMap<ObjectMap<Char, GrammarSymbol>, ObjectMap<Char, GrammarSymbol>>()

		fun reset()
		{
			areaMap.clear()
			defineMap.clear()
			variableMap.clear()
			symbolMap.clear()
		}
	}
}