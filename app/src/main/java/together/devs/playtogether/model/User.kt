package together.devs.playtogether.model

data class User(
    val userName: String,
    val available: Boolean = false,
    val teams: List<String> = emptyList()
)
