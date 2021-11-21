package com.awwsmm.cookies

import org.scalajs.dom
import org.scalajs.dom.document
import scalatags.JsDom.all._

import scala.Console.err

object Main extends App {

  val setKey = input(
    `type` := "text",
    id := "setKey",
    padding := 0,
    border := "1px solid green"
  ).render

  val setValue = input(
    `type` := "text",
    id := "setValue",
    padding := 0,
    border := "1px solid red"
  ).render

  Set(setValue, setKey).foreach { input =>

    // prevent the user from typing ';' or '=' into the input
    input.addEventListener("keypress", (e: dom.KeyboardEvent) => {
      if (e.key == ";" || e.key == "=") {
        e.preventDefault()
      }
    })

    // prevent the user from pasting ';' or '=' into the input
    input.addEventListener("paste", (e: dom.ClipboardEvent) => {
      val text = e.clipboardData.getData("text")
      if (text.contains(";") || text.contains("=")) {
        e.preventDefault()
      }
    })
  }

  def clearCookie(key: String): Unit =
    document.cookie = s"$key=; expires=Thu, 01 Jan 1970 00:00:00 GMT; path=/";

  def clearAllCookies(): Unit = {
    val Cookie = "([^=]+)=.+".r
    document.cookie.split("; ").foreach {
      case Cookie(key) => clearCookie(key)
      case other => err.println(s"Couldn't parse '$other' as a key=value cookie pair")
    }
  }

  val equals = button(
    onclick := { () =>
      if (setValue.value.isEmpty) {
        clearCookie(setKey.value)
      } else if (setKey.value.nonEmpty) {
        document.cookie = s"${setKey.value}=${setValue.value}; expires=Thu, 18 Dec 3000 12:00:00 UTC; path=/";
      }
      cookies.value = document.cookie
    },
    " = "
  ).render

  val clear = button(
    onclick := { () =>
      clearAllCookies()
      cookies.value = document.cookie
    },
    "clear all cookies"
  ).render

  val cookies = textarea(
    readonly,
    rows := 15,
    cols := 50
  ).render

  val container = div(
    style :=
      """position: absolute;
        |top: 50%;
        |left: 50%;
        |-moz-transform: translateX(-50%) translateY(-50%);
        |-webkit-transform: translateX(-50%) translateY(-50%);
        |transform: translateX(-50%) translateY(-50%);
        |border: 1px solid black;
        |width: 75%;
        |height: 75%;
        |padding: 1%;
        |text-align: center
        |""".stripMargin,
    h2("Scala.js browser cookies"),
    p("Set a key equal to a value (no '=' or ';' allowed in either)"),
    p("If the value is empty, the key will be deleted"),
    p("Close and reopen this page to test cookie persistence"),
    div(
      style :=
        """
          |margin: 10px 0 10px 0
          |""".stripMargin,
      div(
        style :=
          """width: 48%;
            |display: inline-block;
            |text-align: right;
            |margin: 0 5px 0 -5px;
            |""".stripMargin,
        setKey
      ),
      div(
        style :=
          """
            |width: 4%;
            |display: inline-block;
            |""".stripMargin,
        equals
      ),
      div(
        style :=
          """width: 48%;
            |display: inline-block;
            |text-align: left;
            |margin: 0 -5px 0 5px;
            |""".stripMargin,
        setValue
      )
    ),
    div(
      cookies
    ),
    div(
      clear
    )
  ).render

  document.addEventListener("DOMContentLoaded", (_: dom.Event) => {
    document.body.appendChild(container)
    cookies.value = document.cookie
  })
}
