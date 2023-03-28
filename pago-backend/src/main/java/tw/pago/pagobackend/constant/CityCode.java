package tw.pago.pagobackend.constant;


import lombok.Getter;

@Getter
public enum CityCode {
  TPE("台北", "Taipei"),
  TKO("東京", "Tokyo"),
  PUS("釜山", "Busan")





  ;

  private String chineseName;
  private String englishName;

  CityCode(String chineseName, String englishName) {
    this.chineseName = chineseName;
    this.englishName = englishName;
  }

  public static CityCode getEnumByEnglishName(String englishName) {
    for (CityCode cityCode : values()) {
      if (cityCode.getEnglishName().equalsIgnoreCase(englishName)) {
        return cityCode;
      }
    }
    throw new IllegalArgumentException("No CityCodeEnum found for English name: " + englishName);
  }

}
