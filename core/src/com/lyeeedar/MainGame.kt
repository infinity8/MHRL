package com.lyeeedar

import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import com.lyeeedar.Screens.*

import javax.swing.*
import java.io.PrintWriter
import java.io.StringWriter
import java.util.HashMap

class MainGame : Game()
{
	enum class ScreenEnum
	{
		TEST
	}

	private val screens = HashMap<ScreenEnum, AbstractScreen>()

	override fun create()
	{
		Global.applicationChanger.processResources()
		Global.setup()

		if (Global.android)
		{

		}
		else
		{
			val sw = StringWriter()
			val pw = PrintWriter(sw)

			val handler = Thread.UncaughtExceptionHandler { myThread, e ->
				e.printStackTrace(pw)
				val exceptionAsString = sw.toString()

				val file = Gdx.files.local("error.log")
				file.writeString(exceptionAsString, false)

				JOptionPane.showMessageDialog(null, "A fatal error occurred. Please send the error.log to me so that I can fix it.", "An error occurred", JOptionPane.ERROR_MESSAGE)

				e.printStackTrace()
			}

			Thread.currentThread().uncaughtExceptionHandler = handler
		}

		screens.put(ScreenEnum.TEST, TestCombatScreen())

		if (Global.PARTICLE_EDITOR)
		{
			setScreen(ParticleEditorScreen())
		}
		else
		{
			switchScreen(ScreenEnum.TEST)
		}
	}

	fun switchScreen(screen: AbstractScreen)
	{
		this.setScreen(screen)
	}

	fun switchScreen(screen: ScreenEnum)
	{
		this.setScreen(screens[screen])
	}
}
