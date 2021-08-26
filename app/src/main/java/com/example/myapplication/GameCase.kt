package com.example.myapplication

class GameCase constructor(
    var position: Int,
    var state: GameCaseState) {
}

enum class GameCaseState {
    None,
    Fire,
    Water,
    Wind,
    Earth
}
