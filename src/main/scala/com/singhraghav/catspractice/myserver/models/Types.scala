//package com.singhraghav.catspractice.myserver.models
//
//import cats.data.NonEmptySet
//import eu.timepit.refined.W
//import eu.timepit.refined.api.Refined
//import eu.timepit.refined.collection.NonEmpty
//import eu.timepit.refined.string.MatchesRegex
//import io.circe.{Decoder, Encoder}
//import io.circe.generic.auto.{exportDecoder, exportEncoder}
//
//object Types {
//
//  private type LanguageCode = String Refined MatchesRegex[W.`"^[a-z]{2}$"`.T]
//
//  private type ProductName = String Refined NonEmpty
//
//  case class Translation(lang: LanguageCode, name: ProductName)
//
//  object Translation {
//    implicit val decode: Decoder[Translation] = Decoder.forProduct2("lang", "name")(Translation.apply)
//    implicit val encode: Encoder[Translation] = Encoder.forProduct2("lang", "name")(t => (t.lang, t.name))
//  }
//
//  private type ProductId = java.util.UUID
//  final case class Product(id: ProductId, names: NonEmptySet[Translation])
//
//
//}
