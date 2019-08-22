package com.play.store.models

case class Id[T](id: Long) {
  override def toString = id.toString
}
