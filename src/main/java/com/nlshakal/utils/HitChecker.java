package com.nlshakal.utils;

import java.math.BigDecimal;

public class HitChecker {
  public boolean isHit(BigDecimal x, BigDecimal y, BigDecimal r) {
    return isRectangleHit(x, y, r) || isCircleHit(x, y, r) || isTriangleHit(x, y, r);
  }

  public boolean isRectangleHit(BigDecimal x, BigDecimal y, BigDecimal r) {
    BigDecimal halfR = r.divide(new BigDecimal("2"));
    return x.compareTo(halfR.negate()) >= 0 && x.compareTo(BigDecimal.ZERO) <= 0
        && y.compareTo(BigDecimal.ZERO) >= 0 && y.compareTo(r) <= 0;
  }

  public boolean isCircleHit(BigDecimal x, BigDecimal y, BigDecimal r) {
    if (x.compareTo(BigDecimal.ZERO) < 0 || y.compareTo(BigDecimal.ZERO) > 0) {
      return false;
    }
    BigDecimal distanceSquared = x.multiply(x).add(y.multiply(y));
    BigDecimal rSquared = r.multiply(r);
    return distanceSquared.compareTo(rSquared) <= 0;
  }

  public boolean isTriangleHit(BigDecimal x, BigDecimal y, BigDecimal r) {
    BigDecimal halfR = r.divide(new BigDecimal("2"));

    if (x.compareTo(BigDecimal.ZERO) < 0 || y.compareTo(BigDecimal.ZERO) < 0) {
      return false;
    }

    return x.compareTo(BigDecimal.ZERO) >= 0 && y.compareTo(BigDecimal.ZERO) >= 0
        && x.compareTo(halfR) <= 0 && y.compareTo(halfR) <= 0
        && x.add(y).compareTo(halfR) <= 0;
  }
}
