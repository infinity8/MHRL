package com.lyeeedar.Level

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.math.MathUtils
import com.lyeeedar.Direction
import com.lyeeedar.SpaceSlot
import com.lyeeedar.Util.Array2D
import com.lyeeedar.Util.Colour
import com.lyeeedar.Util.Point

class Level
{
	var grid: Array2D<Tile> = Array2D()
		set(value)
		{
			field = value

			for (x in 0..width-1)
			{
				for (y in 0..height-1)
				{
					val tile = grid[x, y]

					tile.level = this
					tile.x = x
					tile.y = y

					for (dir in Direction.Values)
					{
						tile.neighbours.put( dir, getTile(x, y, dir) )
					}
				}
			}

			recalculateGrids()
		}

	var doubleGrid: Array<DoubleArray> = Array(0) { i -> DoubleArray(0) { i -> 0.0 } }
	var charGrid: Array<CharArray> = Array(0) { i -> CharArray(0) { i -> '.' } }

	fun recalculateGrids()
	{
		doubleGrid = Array(width) { i -> DoubleArray(height) { i -> 0.0 } }
		charGrid = Array(width) { i -> CharArray(height) { i -> '.' } }

		for (x in 0..width-1)
		{
			for (y in 0..height - 1)
			{
				if (grid[x, y].contents.containsKey(SpaceSlot.WALL))
				{
					doubleGrid[x][y] = 1.0
					charGrid[x][y] = '#'
				}
			}
		}
	}

	lateinit var player: Entity

	val width: Int
		get() = grid.xSize

	val height: Int
		get() = grid.ySize

	val ambient: Colour = Colour(0.1f, 0.1f, 0.1f, 1f)

	// ----------------------------------------------------------------------
	inline fun getTileClamped(point: Point) = getTile(MathUtils.clamp(point.x, 0, width-1), MathUtils.clamp(point.y, 0, height-1))!!

	// ----------------------------------------------------------------------
	inline fun getTile(point: Point) = getTile(point.x, point.y)

	// ----------------------------------------------------------------------
	inline fun getTile(point: Point, ox:Int, oy:Int) = getTile(point.x + ox, point.y + oy)

	// ----------------------------------------------------------------------
	inline fun getTile(point: Point, o: Point) = getTile(point.x + o.x, point.y + o.y)

	// ----------------------------------------------------------------------
	inline fun getTile(x: Int, y: Int, dir: Direction) = getTile(x + dir.x, y + dir.y)

	// ----------------------------------------------------------------------
	inline fun getTile(point: Point, dir: Direction) = getTile(point.x + dir.x, point.y + dir.y)

	// ----------------------------------------------------------------------
	fun getTile(x: Int, y: Int): Tile? = grid[x, y, null]
}