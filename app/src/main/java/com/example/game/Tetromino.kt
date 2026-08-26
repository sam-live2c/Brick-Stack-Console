package com.example.game

enum class TetrominoType(val id: Int) {
    I(1),
    J(2),
    L(3),
    O(4),
    S(5),
    T(6),
    Z(7);

    companion object {
        fun fromId(id: Int): TetrominoType? = values().firstOrNull { it.id == id }
    }
}

data class BlockPosition(val x: Int, val y: Int)

class Tetromino(
    val type: TetrominoType,
    val rotation: Int = 0,
    val position: BlockPosition = BlockPosition(3, 0)
) {
    // 4x4 matrix representation for each shape and rotation state
    val shape: Array<IntArray>
        get() = SHAPES[type]!![rotation % 4]

    fun rotatedClockwise(): Tetromino {
        return Tetromino(type, (rotation + 1) % 4, position)
    }

    fun rotatedCounterClockwise(): Tetromino {
        return Tetromino(type, (rotation + 3) % 4, position)
    }

    fun moved(dx: Int, dy: Int): Tetromino {
        return Tetromino(type, rotation, BlockPosition(position.x + dx, position.y + dy))
    }

    // Get list of absolute board coordinates occupied by this piece
    fun getOccupiedCells(): List<BlockPosition> {
        val cells = mutableListOf<BlockPosition>()
        val matrix = shape
        for (r in matrix.indices) {
            for (c in matrix[r].indices) {
                if (matrix[r][c] != 0) {
                    cells.add(BlockPosition(position.x + c, position.y + r))
                }
            }
        }
        return cells
    }

    companion object {
        fun create(type: TetrominoType, startX: Int = 3, startY: Int = 0): Tetromino {
            return Tetromino(type, 0, BlockPosition(startX, startY))
        }

        // Shape matrices: 1 represents filled block
        private val SHAPES = mapOf(
            TetrominoType.I to arrayOf(
                arrayOf(
                    intArrayOf(0, 0, 0, 0),
                    intArrayOf(1, 1, 1, 1),
                    intArrayOf(0, 0, 0, 0),
                    intArrayOf(0, 0, 0, 0)
                ),
                arrayOf(
                    intArrayOf(0, 0, 1, 0),
                    intArrayOf(0, 0, 1, 0),
                    intArrayOf(0, 0, 1, 0),
                    intArrayOf(0, 0, 1, 0)
                ),
                arrayOf(
                    intArrayOf(0, 0, 0, 0),
                    intArrayOf(0, 0, 0, 0),
                    intArrayOf(1, 1, 1, 1),
                    intArrayOf(0, 0, 0, 0)
                ),
                arrayOf(
                    intArrayOf(0, 1, 0, 0),
                    intArrayOf(0, 1, 0, 0),
                    intArrayOf(0, 1, 0, 0),
                    intArrayOf(0, 1, 0, 0)
                )
            ),
            TetrominoType.J to arrayOf(
                arrayOf(
                    intArrayOf(1, 0, 0),
                    intArrayOf(1, 1, 1),
                    intArrayOf(0, 0, 0)
                ),
                arrayOf(
                    intArrayOf(0, 1, 1),
                    intArrayOf(0, 1, 0),
                    intArrayOf(0, 1, 0)
                ),
                arrayOf(
                    intArrayOf(0, 0, 0),
                    intArrayOf(1, 1, 1),
                    intArrayOf(0, 0, 1)
                ),
                arrayOf(
                    intArrayOf(0, 1, 0),
                    intArrayOf(0, 1, 0),
                    intArrayOf(1, 1, 0)
                )
            ),
            TetrominoType.L to arrayOf(
                arrayOf(
                    intArrayOf(0, 0, 1),
                    intArrayOf(1, 1, 1),
                    intArrayOf(0, 0, 0)
                ),
                arrayOf(
                    intArrayOf(0, 1, 0),
                    intArrayOf(0, 1, 0),
                    intArrayOf(0, 1, 1)
                ),
                arrayOf(
                    intArrayOf(0, 0, 0),
                    intArrayOf(1, 1, 1),
                    intArrayOf(1, 0, 0)
                ),
                arrayOf(
                    intArrayOf(1, 1, 0),
                    intArrayOf(0, 1, 0),
                    intArrayOf(0, 1, 0)
                )
            ),
            TetrominoType.O to arrayOf(
                arrayOf(
                    intArrayOf(1, 1),
                    intArrayOf(1, 1)
                ),
                arrayOf(
                    intArrayOf(1, 1),
                    intArrayOf(1, 1)
                ),
                arrayOf(
                    intArrayOf(1, 1),
                    intArrayOf(1, 1)
                ),
                arrayOf(
                    intArrayOf(1, 1),
                    intArrayOf(1, 1)
                )
            ),
            TetrominoType.S to arrayOf(
                arrayOf(
                    intArrayOf(0, 1, 1),
                    intArrayOf(1, 1, 0),
                    intArrayOf(0, 0, 0)
                ),
                arrayOf(
                    intArrayOf(0, 1, 0),
                    intArrayOf(0, 1, 1),
                    intArrayOf(0, 0, 1)
                ),
                arrayOf(
                    intArrayOf(0, 0, 0),
                    intArrayOf(0, 1, 1),
                    intArrayOf(1, 1, 0)
                ),
                arrayOf(
                    intArrayOf(1, 0, 0),
                    intArrayOf(1, 1, 0),
                    intArrayOf(0, 1, 0)
                )
            ),
            TetrominoType.T to arrayOf(
                arrayOf(
                    intArrayOf(0, 1, 0),
                    intArrayOf(1, 1, 1),
                    intArrayOf(0, 0, 0)
                ),
                arrayOf(
                    intArrayOf(0, 1, 0),
                    intArrayOf(0, 1, 1),
                    intArrayOf(0, 1, 0)
                ),
                arrayOf(
                    intArrayOf(0, 0, 0),
                    intArrayOf(1, 1, 1),
                    intArrayOf(0, 1, 0)
                ),
                arrayOf(
                    intArrayOf(0, 1, 0),
                    intArrayOf(1, 1, 0),
                    intArrayOf(0, 1, 0)
                )
            ),
            TetrominoType.Z to arrayOf(
                arrayOf(
                    intArrayOf(1, 1, 0),
                    intArrayOf(0, 1, 1),
                    intArrayOf(0, 0, 0)
                ),
                arrayOf(
                    intArrayOf(0, 0, 1),
                    intArrayOf(0, 1, 1),
                    intArrayOf(0, 1, 0)
                ),
                arrayOf(
                    intArrayOf(0, 0, 0),
                    intArrayOf(1, 1, 0),
                    intArrayOf(0, 1, 1)
                ),
                arrayOf(
                    intArrayOf(0, 1, 0),
                    intArrayOf(1, 1, 0),
                    intArrayOf(1, 0, 0)
                )
            )
        )
    }
}
