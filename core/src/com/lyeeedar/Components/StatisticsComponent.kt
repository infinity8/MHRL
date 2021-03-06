package com.lyeeedar.Components

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.utils.ObjectFloatMap
import com.badlogic.gdx.utils.OrderedSet
import com.badlogic.gdx.utils.XmlReader
import com.lyeeedar.ElementType
import com.lyeeedar.Util.FastEnumMap
import com.lyeeedar.Util.ciel
import ktx.collections.toGdxArray

class StatisticsComponent: AbstractComponent()
{
	val factions: OrderedSet<String> = OrderedSet()

	var hp: Float = 0f
		get() = field
		set(value)
		{
			var v = Math.min(value, maxHP)

			var diff = v - hp
			if (diff < 0)
			{
				if (invulnerable)
				{
					blockedDamage = true
					return
				}
				else if (blocking)
				{
					stamina += diff
					if (stamina < 0)
					{
						blocking = false
						v = hp + stamina

						blockedDamage = false
						blockBroken = true
					}
					else
					{
						blockedDamage = true
					}
				}
			}

			field = v
			if (godMode && field < 1) field = 1f
		}

	var maxHP: Float = 0f
		get() = field
		set(value)
		{
			field = value

			if (hp < value) hp = value
		}

	var stamina: Float = 0f
		get() = field
		set(value)
		{
			val v = Math.min(maxStamina, value)
			field = v
		}

	var maxStamina: Float = 0f
		get() = field
		set(value)
		{
			field = value

			if (stamina < value) stamina = value
		}

	val resistances = FastEnumMap<ElementType, Int>(ElementType::class.java)

	var sight: Float = 0f

	var blocking = false
	var invulnerable = false
	var godMode = false

	var blockedDamage = false
	var blockBroken = false
	var insufficientStamina = 0f

	override fun parse(xml: XmlReader.Element, entity: Entity)
	{
		factions.addAll(xml.get("Faction").split(",").toGdxArray())
		maxHP += xml.getInt("HP")
		maxStamina += xml.getInt("Stamina")
		sight += xml.getInt("Sight")
	}

	fun dealDamage(amount: Int, element: ElementType, elementalConversion: Float)
	{
		var elementalDam = (amount * elementalConversion).ciel()
		val baseDam = amount - elementalDam

		hp -= baseDam

		val resistance = resistances[element] ?: 0
		elementalDam += (elementalDam.toFloat() * 0.25f * resistance.toFloat()).ciel()

		hp -= elementalDam
	}

	operator fun get(key: String, fallback: Float? = null): Float
	{
		return when (key.toLowerCase())
		{
			"hp" -> hp
			"maxhp" -> maxHP
			"stamina" -> stamina
			"maxstamina" -> maxStamina
			else -> fallback ?: throw Exception("Unknown statistic '$key'!")
		}
	}

	fun write(variableMap: ObjectFloatMap<String>): ObjectFloatMap<String>
	{
		variableMap.put("hp", hp)
		variableMap.put("maxhp", maxHP)
		variableMap.put("stamina", stamina)
		variableMap.put("maxstamina", maxStamina)

		return variableMap
	}
}

fun OrderedSet<String>.isAllies(other: OrderedSet<String>): Boolean
{
	for (faction in this)
	{
		if (other.contains(faction)) return true
	}

	return false
}