package net.gesekus.housekeeping.argonaut

import argonaut.Argonaut.{JsonField, jString}
import argonaut.{DecodeJson, DecodeResult}

object ArgonautHelper {
  implicit def MapKVDecodeJson[K,V](implicit e: DecodeJson[K],  f: DecodeJson[V]): DecodeJson[Map[K, V]] = {
    DecodeJson(a =>
      a.fields match {
        case None => DecodeResult.fail("[K, V]Map[K, V]", a.history)
        case Some(s) => {
          def spin(x: List[JsonField], m: DecodeResult[Map[K, V]]): DecodeResult[Map[K, V]] = {
            x match {
              case Nil => m
              case h::t => {
                spin(t, for {
                  mm <- m
                  v <- a.get(h)(f)
                  k <- e.decodeJson(jString(h))
                } yield mm + ((k, v)))
              }
            }
          }
          spin(s, DecodeResult.ok(Map.empty[K, V]))
        }
      }
    )
  }
}
