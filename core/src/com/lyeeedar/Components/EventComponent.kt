package com.lyeeedar.Components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.XmlReader
import com.lyeeedar.Events.EventActionGroup
import com.lyeeedar.Events.EventArgs
import com.lyeeedar.Util.FastEnumMap

/**
 * Created by Philip on 22-Mar-16.
 */

class EventComponent: Component
{
	constructor()
	{
		for (type in EventType.values())
		{
			handlers.put(type, com.badlogic.gdx.utils.Array<EventActionGroup>())
		}
	}

	enum class EventType
	{
		TURN,
		MOVE,
		ATTACK,
		WAIT,

		DEALDAMAGE,
		RECEIVEDAMAGE,
		HEAL,

		SPAWN,
		DEATH,

		ACTIVATE,
		NONE,
		ALL
	}

	val handlers: FastEnumMap<EventType, com.badlogic.gdx.utils.Array<EventActionGroup>> = FastEnumMap(EventType::class.java)
	val pendingEvents: com.badlogic.gdx.utils.Array<EventArgs> = com.badlogic.gdx.utils.Array(false, 8)

	fun registerHandler(type: EventType, handler:EventActionGroup)
	{
		handlers.get(type).add(handler)
		if (type != EventType.ALL) handlers.get(EventType.ALL).add(handler)
	}

	fun parse(xml: XmlReader.Element)
	{
		for (i in 0..xml.childCount-1)
		{
			val typeBlock = xml.getChild(i)
			val blockName = typeBlock.name.toUpperCase()

			if (!blockName.startsWith("ON")) throw RuntimeException("Invalid event type: $blockName")

			val type = EventType.valueOf(blockName.substring(2))

			for (g in 0..typeBlock.childCount-1)
			{
				val groupEl = typeBlock.getChild(g)
				val group = EventActionGroup()
				group.parse(groupEl)

				registerHandler(type, group)
			}
		}
	}
}