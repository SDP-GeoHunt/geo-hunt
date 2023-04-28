import com.github.geohunt.app.R

class TutorialSlides(
    val image: Int,
    val title: Int,
    val description: Int
) {
    companion object{
        fun getData(): List<TutorialSlides>{
            return listOf(
                TutorialSlides(
                    R.drawable.tutorial_icon_1,
                    R.string.tutorial_first_screen_title,
                    R.string.tutorial_first_screen_description
                ),
                TutorialSlides(
                    R.drawable.tutorial_icon_2,
                    R.string.tutorial_second_screen_title,
                    R.string.tutorial_second_screen_description
                ),
                TutorialSlides(
                    R.drawable.tutorial_icon_3,
                    R.string.tutorial_third_screen_title,
                    R.string.tutorial_third_screen_description
                )
            )
        }
    }
}