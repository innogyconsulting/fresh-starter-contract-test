package energy.getfresh.test.contract;

import javax.persistence.Entity;

/**
 * @author morisil
 */
@Entity
public class Foo {

  private long id;

  private String bar;

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getBar() {
    return bar;
  }

  public void setBar(String bar) {
    this.bar = bar;
  }

}
