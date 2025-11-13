package com.nlshakal.beans;

import java.io.Serializable;

public class ResultBean implements Serializable {
  private String x;
  private String y;
  private String r;
  private boolean hit;
  private String currentTime;
  private String executionTime;

  public ResultBean(String x, String y, String r, boolean hit,
                    String currentTime, String executionTime) {
    this.x = x;
    this.y = y;
    this.r = r;
    this.hit = hit;
    this.currentTime = currentTime;
    this.executionTime = executionTime;
  }

  public String getX() { return x; }
  public String getY() { return y; }
  public String getR() { return r; }
  public boolean isHit() { return hit; }
  public String getCurrentTime() { return currentTime; }
  public String getExecutionTime() { return executionTime; }
}
