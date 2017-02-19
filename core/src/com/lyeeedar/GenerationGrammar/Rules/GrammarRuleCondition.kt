package com.lyeeedar.GenerationGrammar.Rules

import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.ObjectFloatMap
import com.badlogic.gdx.utils.ObjectMap
import com.badlogic.gdx.utils.XmlReader
import com.exp4j.Helpers.evaluate
import com.lyeeedar.GenerationGrammar.Area
import com.lyeeedar.GenerationGrammar.GrammarSymbol
import com.lyeeedar.Util.children
import java.util.*

class GrammarRuleCondition : AbstractGrammarRule()
{
	val conditions = Array<Condition>()

	override fun execute(area: Area, ruleTable: ObjectMap<String, AbstractGrammarRule>, defines: ObjectMap<String, String>, variables: ObjectFloatMap<String>, symbolTable: ObjectMap<Char, GrammarSymbol>, ran: Random)
	{
		for (condition in conditions)
		{
			if (condition.condition == "else" || condition.condition.evaluate(variables, ran) > 0)
			{
				if (!condition.rule.isNullOrBlank())
				{
					val rule = ruleTable[condition.rule]
					rule.execute(area, ruleTable, defines, variables, symbolTable, ran)
				}

				break
			}
		}
	}

	override fun parse(xml: XmlReader.Element)
	{
		for (el in xml.children())
		{
			val condition = xml.get("Condition").toLowerCase()
			val rule = xml.get("Rule", "")

			conditions.add(Condition(condition, rule))
		}
	}

}

data class Condition(val condition: String, val rule: String)