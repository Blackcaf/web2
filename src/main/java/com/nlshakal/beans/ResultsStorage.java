package com.nlshakal.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ResultsStorage implements Serializable {
  private final List<ResultBean> results = new ArrayList<>();

  public void addResult(ResultBean result) {
    results.add(0, result);
  }

  public List<ResultBean> getResults() {
    return Collections.unmodifiableList(results);
  }

  public void clear() {
    results.clear();
  }
}
