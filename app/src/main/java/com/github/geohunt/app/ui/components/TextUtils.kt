package com.github.geohunt.app.ui.components

import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.*
import androidx.compose.ui.text.style.TextDecoration
import com.github.geohunt.app.ui.theme.Typography

data class LinkTextData(
        val text: String,
        val tag: String ?= null,
        val annotation: String ?= null,
        val onClick: ((str: AnnotatedString.Range<String>) -> Unit) ?= null,
)

@Composable
fun LinkText(
        linkTextData: List<LinkTextData>,
        modifier: Modifier = Modifier,
        style: TextStyle = Typography.body1,
        primaryColor: Color = MaterialTheme.colors.primary,
) {
    val annotatedString = createAnnotatedString(linkTextData, primaryColor)

    ClickableText(
            text = annotatedString,
            style = style,
            onClick = { offset ->
                linkTextData.forEach { annotatedStringData ->
                    if (annotatedStringData.tag != null && annotatedStringData.annotation != null) {
                        annotatedString.getStringAnnotations(
                                tag = annotatedStringData.tag,
                                start = offset,
                                end = offset,
                        ).firstOrNull()?.let {
                            annotatedStringData.onClick?.invoke(it)
                        }
                    }
                }
            },
            modifier = modifier,
    )
}

@Composable
private fun createAnnotatedString(data: List<LinkTextData>, primaryColor: Color): AnnotatedString {
    return buildAnnotatedString {
        data.forEach { linkTextData ->
            if (linkTextData.tag != null && linkTextData.annotation != null) {
                pushStringAnnotation(
                        tag = linkTextData.tag,
                        annotation = linkTextData.annotation,
                )
                withStyle(
                        style = SpanStyle(
                                color = primaryColor,
                                textDecoration = TextDecoration.Underline,
                        ),
                ) {
                    append(linkTextData.text)
                }
                pop()
            } else {
                append(linkTextData.text)
            }
        }
    }
}

