package com.nvrskdev.myschool

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

enum class Screen(val route: String, @StringRes val labelId: Int, @DrawableRes val iconId: Int) {
    News("news", R.string.news_nav_item, R.drawable.newspaper_24px),
    Schedule("schedule", R.string.schedule_nav_item, R.drawable.calendar_month_24px),
    Progress("progress", R.string.progress_nav_item, R.drawable.show_chart_24px),
    Teachers("teachers", R.string.teachers_nav_item, R.drawable.groups_24px),
    Tests("tests", R.string.tests_nav_item, R.drawable.quiz_24px),
    NewTest("new_test", R.string.new_test_nav_item, R.drawable.description_24px),
    TeacherDetails("teacher_details", R.string.teacher_details_nav_item, R.drawable.person_24px),
    Journal("journal", R.string.journal_nav_item, R.drawable.list_alt_24px),
    TestPassing("test_passing", R.string.test_passing_nav_item, R.drawable.quiz_24px)
}
