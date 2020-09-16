package com.jobayr.arxiv.activities

import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import com.google.android.material.chip.Chip
import com.jobayr.arxiv.R
import com.jobayr.arxiv.extensions.*
import com.kaopiz.kprogresshud.KProgressHUD
import kotlinx.android.synthetic.main.activity_search.*
import okhttp3.*
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException
import java.lang.IllegalStateException
import java.text.ParseException

class SearchActivity : AppCompatActivity() {

    private var ssEverywhereSelected = true
    private var ssSelectedFI: MutableList<String> = mutableListOf()
    private lateinit var waitingDialog: KProgressHUD

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        initToolbar(getString(R.string.search))
        init()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) onBackPressed()
        return super.onOptionsItemSelected(item)
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val v: View? = currentFocus
            if (v is EditText) {
                val outRect = Rect()
                v.getGlobalVisibleRect(outRect)
                if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                    v.clearFocus()
                    val imm: InputMethodManager =
                        getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0)
                }
            }
        }
        return super.dispatchTouchEvent(event)
    }

    private fun init() {
        waitingDialog = waitingDialog()
        initOnClick()
    }

    private fun initOnClick() {
        settingsSearchBtnGroup.addOnButtonCheckedListener { _, checkedId, isChecked ->
            when (checkedId) {
                R.id.settingsSearchBtnSimple -> {
                    if (isChecked) {
                        settingsSearchBtnSimple.setBackgroundColor(
                            ResourcesCompat.getColor(
                                resources,
                                R.color.colorBlack,
                                theme
                            )
                        )
                        settingsSearchBtnSimple.setTextColor(
                            ResourcesCompat.getColor(
                                resources,
                                R.color.colorWhite,
                                theme
                            )
                        )
                        settingsSearchBtnAdvanced.setBackgroundColor(
                            ResourcesCompat.getColor(
                                resources,
                                R.color.colorWhite,
                                theme
                            )
                        )
                        settingsSearchBtnAdvanced.setTextColor(
                            ResourcesCompat.getColor(
                                resources,
                                R.color.colorBlack,
                                theme
                            )
                        )
                        searchAdvancedLayoutContainer.makeGone()
                        searchSimpleLayoutContainer.makeVisible()
                    }
                }
                R.id.settingsSearchBtnAdvanced -> {
                    if (isChecked) {
                        settingsSearchBtnSimple.setBackgroundColor(
                            ResourcesCompat.getColor(
                                resources,
                                R.color.colorWhite,
                                theme
                            )
                        )
                        settingsSearchBtnSimple.setTextColor(
                            ResourcesCompat.getColor(
                                resources,
                                R.color.colorBlack,
                                theme
                            )
                        )
                        settingsSearchBtnAdvanced.setBackgroundColor(
                            ResourcesCompat.getColor(
                                resources,
                                R.color.colorBlack,
                                theme
                            )
                        )
                        settingsSearchBtnAdvanced.setTextColor(
                            ResourcesCompat.getColor(
                                resources,
                                R.color.colorWhite,
                                theme
                            )
                        )
                        searchSimpleLayoutContainer.makeGone()
                        searchAdvancedLayoutContainer.makeVisible()
                    }
                }
            }
        }
        initChipCallback()
        searchSearchKeywordInput.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(p0: Editable?) {
                p0?.let {
                    searchSimpleSearchBtn.isEnabled = it.toString().isNotBlank()
                }
            }

        })
        searchSimpleSearchBtn.onClick {
            doSomething()
        }
    }

    private fun doSomething() {

        val client = OkHttpClient()
        val builder = Request.Builder()
            .url("http://export.arxiv.org/api/query?search_query=all:electron&start=0&max_results=8")
            .build()
        client.newCall(builder).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("ApiError", "Error 2 -> ${e.message}" )
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    if (response.body != null) {
                        try {
                            val data = getFeed(response.body!!.byteStream())
                            Log.e("ApiError", data.toString())
                        } catch (e: XmlPullParserException ) {
                            Log.e("ApiError", "Error 3 -> ${e.message}" )
                        } catch (e2: IOException) {
                            Log.e("ApiError", "Error 4 -> ${e2.message}" )
                        } catch (e3: ParseException) {
                            Log.e("ApiError", "Error 5 -> ${e3.message}" )
                        } catch (e4: IllegalStateException) {
                            Log.e("ApiError", "Error 6 -> ${e4.message}" )
                        } catch (e5: Exception) {
                            Log.e("ApiError", "Error 7 -> ${e5.message}" )
                        }
                    } else Log.e("ApiError", "Response Body Error")
                    response.body?.let {it1 ->
                        val data = getFeed(it1.byteStream())
                        Log.e("ApiError", data.toString())
                    }
                } else {
                    Log.e("ApiError", "Error 1 -> ${response.message}" )
                }
            }
        })
    }

    private fun initChipCallback() {
        searchSimpleSearchFIEverywhere.setOnClickListener {
            if ((it as Chip).isChecked) {
                ssEverywhereSelected = true
                searchSimpleSearchFITitle.isChecked = true
                searchSimpleSearchFIComment.isChecked = true
                searchSimpleSearchFIAbstract.isChecked = true
                searchSimpleSearchFIAuthor.isChecked = true
                searchSimpleSearchFIJournalRef.isChecked = true
                searchSimpleSearchFISubCat.isChecked = true
                if (!ssSelectedFI.contains("ti")) {
                    ssSelectedFI.add("ti")
                }
                if (!ssSelectedFI.contains("au")) {
                    ssSelectedFI.add("au")
                }
                if (!ssSelectedFI.contains("abs")) {
                    ssSelectedFI.add("abs")
                }
                if (!ssSelectedFI.contains("co")) {
                    ssSelectedFI.add("co")
                }
                if (!ssSelectedFI.contains("jr")) {
                    ssSelectedFI.add("jr")
                }
                if (!ssSelectedFI.contains("cat")) {
                    ssSelectedFI.add("cat")
                }
            } else {
                ssEverywhereSelected = false
                searchSimpleSearchFITitle.isChecked = false
                searchSimpleSearchFIComment.isChecked = false
                searchSimpleSearchFIAbstract.isChecked = false
                searchSimpleSearchFIAuthor.isChecked = false
                searchSimpleSearchFIJournalRef.isChecked = false
                searchSimpleSearchFISubCat.isChecked = false
                if (ssSelectedFI.contains("ti")) {
                    ssSelectedFI.remove("ti")
                }
                if (ssSelectedFI.contains("au")) {
                    ssSelectedFI.remove("au")
                }
                if (ssSelectedFI.contains("abs")) {
                    ssSelectedFI.remove("abs")
                }
                if (ssSelectedFI.contains("co")) {
                    ssSelectedFI.remove("co")
                }
                if (ssSelectedFI.contains("jr")) {
                    ssSelectedFI.remove("jr")
                }
                if (ssSelectedFI.contains("cat")) {
                    ssSelectedFI.remove("cat")
                }
            }
        }
        searchSimpleSearchFITitle.setOnClickListener {
            if ((it as Chip).isChecked) {
                if (!ssSelectedFI.contains("ti")) {
                    ssSelectedFI.add("ti")
                }
                if (
                    searchSimpleSearchFIComment.isChecked
                    && searchSimpleSearchFIAbstract.isChecked
                    && searchSimpleSearchFIAuthor.isChecked
                    && searchSimpleSearchFIJournalRef.isChecked
                    && searchSimpleSearchFISubCat.isChecked
                ) {
                    searchSimpleSearchFIEverywhere.isChecked = true
                    ssEverywhereSelected = true
                } else {
                    searchSimpleSearchFIEverywhere.isChecked = false
                    ssEverywhereSelected = false
                }
            } else {
                if (ssSelectedFI.contains("ti")) {
                    ssSelectedFI.remove("ti")
                }
                if (
                    searchSimpleSearchFIComment.isChecked
                    && searchSimpleSearchFIAbstract.isChecked
                    && searchSimpleSearchFIAuthor.isChecked
                    && searchSimpleSearchFIJournalRef.isChecked
                    && searchSimpleSearchFISubCat.isChecked
                ) {
                    searchSimpleSearchFIEverywhere.isChecked = false
                    ssEverywhereSelected = false
                }
            }
        }
        searchSimpleSearchFIComment.setOnClickListener {
            if ((it as Chip).isChecked) {
                if (!ssSelectedFI.contains("co")) {
                    ssSelectedFI.add("co")
                }
                if (
                    searchSimpleSearchFITitle.isChecked
                    && searchSimpleSearchFIAbstract.isChecked
                    && searchSimpleSearchFIAuthor.isChecked
                    && searchSimpleSearchFIJournalRef.isChecked
                    && searchSimpleSearchFISubCat.isChecked
                ) {
                    searchSimpleSearchFIEverywhere.isChecked = true
                    ssEverywhereSelected = true
                } else {
                    searchSimpleSearchFIEverywhere.isChecked = false
                    ssEverywhereSelected = false
                }
            } else {
                if (ssSelectedFI.contains("co")) {
                    ssSelectedFI.remove("co")
                }
                if (
                    searchSimpleSearchFITitle.isChecked
                    && searchSimpleSearchFIAbstract.isChecked
                    && searchSimpleSearchFIAuthor.isChecked
                    && searchSimpleSearchFIJournalRef.isChecked
                    && searchSimpleSearchFISubCat.isChecked
                ) {
                    searchSimpleSearchFIEverywhere.isChecked = false
                    ssEverywhereSelected = false
                }
            }
        }
        searchSimpleSearchFIAbstract.setOnClickListener {
            if ((it as Chip).isChecked) {
                if (!ssSelectedFI.contains("abs")) {
                    ssSelectedFI.add("abs")
                }
                if (
                    searchSimpleSearchFITitle.isChecked
                    && searchSimpleSearchFIComment.isChecked
                    && searchSimpleSearchFIAuthor.isChecked
                    && searchSimpleSearchFIJournalRef.isChecked
                    && searchSimpleSearchFISubCat.isChecked
                ) {
                    searchSimpleSearchFIEverywhere.isChecked = true
                    ssEverywhereSelected = true
                } else {
                    searchSimpleSearchFIEverywhere.isChecked = false
                    ssEverywhereSelected = false
                }
            } else {
                if (ssSelectedFI.contains("abs")) {
                    ssSelectedFI.remove("abs")
                }
                if (
                    searchSimpleSearchFITitle.isChecked
                    && searchSimpleSearchFIComment.isChecked
                    && searchSimpleSearchFIAuthor.isChecked
                    && searchSimpleSearchFIJournalRef.isChecked
                    && searchSimpleSearchFISubCat.isChecked
                ) {
                    searchSimpleSearchFIEverywhere.isChecked = false
                    ssEverywhereSelected = false
                }
            }
        }
        searchSimpleSearchFIAuthor.setOnClickListener {
            if ((it as Chip).isChecked) {
                if (!ssSelectedFI.contains("au")) {
                    ssSelectedFI.add("au")
                }
                if (
                    searchSimpleSearchFITitle.isChecked
                    && searchSimpleSearchFIComment.isChecked
                    && searchSimpleSearchFIAbstract.isChecked
                    && searchSimpleSearchFIJournalRef.isChecked
                    && searchSimpleSearchFISubCat.isChecked
                ) {
                    searchSimpleSearchFIEverywhere.isChecked = true
                    ssEverywhereSelected = true
                } else {
                    searchSimpleSearchFIEverywhere.isChecked = false
                    ssEverywhereSelected = false
                }
            } else {
                if (ssSelectedFI.contains("au")) {
                    ssSelectedFI.remove("au")
                }
                if (
                    searchSimpleSearchFITitle.isChecked
                    && searchSimpleSearchFIComment.isChecked
                    && searchSimpleSearchFIAbstract.isChecked
                    && searchSimpleSearchFIJournalRef.isChecked
                    && searchSimpleSearchFISubCat.isChecked
                ) {
                    searchSimpleSearchFIEverywhere.isChecked = false
                    ssEverywhereSelected = false
                }
            }
        }
        searchSimpleSearchFIJournalRef.setOnClickListener {
            if ((it as Chip).isChecked) {
                if (!ssSelectedFI.contains("jr")) {
                    ssSelectedFI.add("jr")
                }
                if (
                    searchSimpleSearchFITitle.isChecked
                    && searchSimpleSearchFIComment.isChecked
                    && searchSimpleSearchFIAbstract.isChecked
                    && searchSimpleSearchFIAuthor.isChecked
                    && searchSimpleSearchFISubCat.isChecked
                ) {
                    searchSimpleSearchFIEverywhere.isChecked = true
                    ssEverywhereSelected = true
                } else {
                    searchSimpleSearchFIEverywhere.isChecked = false
                    ssEverywhereSelected = false
                }
            } else {
                if (ssSelectedFI.contains("jr")) {
                    ssSelectedFI.remove("jr")
                }
                if (
                    searchSimpleSearchFITitle.isChecked
                    && searchSimpleSearchFIComment.isChecked
                    && searchSimpleSearchFIAbstract.isChecked
                    && searchSimpleSearchFIAuthor.isChecked
                    && searchSimpleSearchFISubCat.isChecked
                ) {
                    searchSimpleSearchFIEverywhere.isChecked = false
                    ssEverywhereSelected = false
                }
            }
        }
        searchSimpleSearchFISubCat.setOnClickListener {
            if ((it as Chip).isChecked) {
                if (!ssSelectedFI.contains("cat")) {
                    ssSelectedFI.add("cat")
                }
                if (
                    searchSimpleSearchFITitle.isChecked
                    && searchSimpleSearchFIComment.isChecked
                    && searchSimpleSearchFIAbstract.isChecked
                    && searchSimpleSearchFIAuthor.isChecked
                    && searchSimpleSearchFIJournalRef.isChecked
                ) {
                    searchSimpleSearchFIEverywhere.isChecked = true
                    ssEverywhereSelected = true
                } else {
                    searchSimpleSearchFIEverywhere.isChecked = false
                    ssEverywhereSelected = false
                }
            } else {
                if (ssSelectedFI.contains("cat")) {
                    ssSelectedFI.remove("cat")
                }
                if (
                    searchSimpleSearchFITitle.isChecked
                    && searchSimpleSearchFIComment.isChecked
                    && searchSimpleSearchFIAbstract.isChecked
                    && searchSimpleSearchFIAuthor.isChecked
                    && searchSimpleSearchFIJournalRef.isChecked
                ) {
                    searchSimpleSearchFIEverywhere.isChecked = false
                    ssEverywhereSelected = false
                }
            }
        }
    }

}