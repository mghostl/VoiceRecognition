package com.mghostl.education.android.voicerecognition

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.ImageView
import android.widget.ListView
import androidx.annotation.DrawableRes
import androidx.appcompat.view.menu.ActionMenuItemView
import androidx.core.graphics.drawable.toBitmap
import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import org.hamcrest.CoreMatchers.anything
import org.hamcrest.Description
import org.hamcrest.TypeSafeMatcher
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class SecondLessonInstrumentedTest {
    @get:Rule
    var activityScenarioRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun testToolbarIsDisplayed() {
        onView(withId(R.id.toolbar))
            .check(matches(isDisplayed()))
    }

    @Test
    fun testToolbarMenuItemStopIsDisplayed() {
        onView(withId(R.id.action_stop))
            .check(matches(isDisplayed()))
    }


    @Test
    fun testToolbarMenuItemStopHasDrawable() {
        onView(withId(R.id.action_stop))
            .check(matches(menuItemWithBackgroundDrawable(R.drawable.ic_baseline_stop_circle_24)))
    }

    @Test
    fun tetToolbarMenuItemClearIsDisplayed() {
        onView(withId(R.id.action_clear))
            .check(matches(isDisplayed()))
    }

    @Test
    fun testToolbarMenuItemClearHasDrawable() {
        onView(withId(R.id.action_clear))
            .check(matches(menuItemWithBackgroundDrawable(R.drawable.ic_baseline_clear_all_24)))
    }

    @Test
    fun testTextInputEditTextIsDisplayed() {
        onView(withId(R.id.text_input_edit))
            .check(matches(isDisplayed()))
    }

    @Test
    fun testTextInputEditTextHasHint() {
        onView(withId(R.id.text_input_edit))
            .check(matches(withHint(R.string.request_hint)))
    }

    @Test
    fun testTextInputEditTextHasImeAction() {
        onView(withId(R.id.text_input_edit))
            .check(matches(hasImeAction(EditorInfo.IME_ACTION_DONE)))
    }

    @Test
    fun testListViewIsDisplayed() {
            onView(withId(R.id.pods_list))
                .check(matches(isDisplayed()))
    }


    @Test
    fun testFabIsDisplayed() {
        onView(withId(R.id.voice_input_button))
            .check(matches(isDisplayed()))
    }

    @Test
    fun testFabHasDrawable() {
        onView(withId(R.id.voice_input_button))
            .check(matches(
                withDrawable(R.drawable.ic_baseline_keyboard_voice_24)
            ))
    }

    private fun withDrawable(@DrawableRes id: Int) = object : TypeSafeMatcher<View>() {
        override fun describeTo(description: Description?) {
            description?.appendText("ImageView with drawable same as drawable with id $id")
        }

        override fun matchesSafely(item: View?): Boolean {
            val expectedBitmap = item?.context?.getDrawable(id)?.let { getBitmapFromDrawable(it) } ?: return false
            val actualBitmap = (item as ImageView?)?.drawable?.let { getBitmapFromDrawable(it) } ?: return false
            return actualBitmap.sameAs(expectedBitmap)
        }

        private fun getBitmapFromDrawable(drawable: Drawable): Bitmap {
            val bitmap = Bitmap.createBitmap(
                drawable.intrinsicWidth,
                drawable.intrinsicHeight,
                Bitmap.Config.ARGB_8888
            )

            val canvas = Canvas(bitmap)

            drawable.apply {
                setBounds(0, 0, canvas.width, canvas.height)
                setTint(Color.BLACK)
                draw(canvas)
            }
            return bitmap
        }
    }

    private fun hasItems(expectedCount: Int) = object : TypeSafeMatcher<View>() {
        override fun describeTo(description: Description?) {
            description?.appendText("ListView has $expectedCount items")
        }

        override fun matchesSafely(item: View?): Boolean {
            val actualCount = (item as ListView?)?.adapter?.count ?: return false
            return expectedCount == actualCount
        }

    }


    private fun menuItemWithBackgroundDrawable(@DrawableRes id: Int) = object : TypeSafeMatcher<View>() {
        override fun describeTo(description: Description?) {
            description?.appendText("ActionMenuItemView with drawable same as drawable with id $id")
        }

        override fun matchesSafely(item: View?): Boolean {
            val expectedBitmap = item?.context?.getDrawable(id)?.toBitmap() ?: return false
            val actualBitmap = (item as ActionMenuItemView?)?.itemData?.icon?.toBitmap() ?: return false
            return actualBitmap.sameAs(expectedBitmap)
        }
    }
}