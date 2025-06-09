package org.entur.enlil.faker;

import net.datafaker.Faker;

public class EnlilFaker extends Faker {
  // Here you can override locale if you like values localized and i8n for your country
  public static final EnlilFaker ENLIL_FAKER = new EnlilFaker();
}