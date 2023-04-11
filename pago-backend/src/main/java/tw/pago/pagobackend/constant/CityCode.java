package tw.pago.pagobackend.constant;

import static com.neovisionaries.i18n.CountryCode.*;

import com.neovisionaries.i18n.CountryCode;
import lombok.Getter;

@Getter
public enum CityCode {
  // TW
  TPE("台北市", "Taipei", TW),
  TPH("新北市", "New Taipei", TW),
  TYC("桃園市", "Taoyuan ", TW),
  TXG("台中市", "Taichung ", TW),
  TNN("台南市", "Tainan", TW),

  // JP
  TKO("東京市", "Tokyo", JP),
  HND("東京羽田", "Tokyo Haneda", JP),
  NRT("東京成田", "Tokyo Narita", JP),
  NGO("名古屋", "Nagoya", JP),
  CTS("札幌市", "Sapporo", JP),
  KIX("大阪市", "Osaka", JP),
  FUK("福岡市", "Fukuoka", JP),

  // KR
  SEL("首爾", "Seoul", KR),
  GMP("首爾金浦", "Seoul Gimpo", KR),
  PUS("釜山", "Busan", KR),
  ICN("仁川", "Incheon", KR),
  CJU("濟州", "Jeju", KR),
  TAE("大邱", "Daegu", KR),
  KWJ("光州", "Gwangju", KR),

  // US
  SFO("舊金山", "San Francisco", US),
  LAX("洛杉磯", "Los Angeles", US),
  JFK("紐約", "New York", US),

  HKG("香港", "Hong Kong", HK),
  LHR("倫敦", "London", GB),
  CDG("巴黎", "Paris", FR),
  FRA("法蘭克福", "Frankfurt", DE),
  AMS("阿姆斯特丹", "Amsterdam", NL),
  SIN("新加坡", "Singapore", SG),
  SYD("悉尼", "Sydney", AU),
  DXB("杜拜", "Dubai", AE),
  YYZ("多倫多", "Toronto", CA),
  BKK("曼谷", "Bangkok", TH),
  KUL("吉隆坡", "Kuala Lumpur", MY),
  MNL("馬尼拉", "Manila", PH),
  IST("伊斯坦堡", "Istanbul", TR),
  GRU("聖保羅", "São Paulo", BR),
  MEX("墨西哥城", "Mexico City", MX),
  SVO("莫斯科", "Moscow", RU),

  // IN
  BOM("孟買", "Mumbai", IN),
  DEL("德里", "Delhi", IN),
  MAA("金奈", "Chennai", IN),
  BLR("邦加羅爾", "Bengaluru", IN),

  // CN
  PEK("北京", "Beijing", CN),
  PVG("上海", "Shanghai", CN),
  CAN("廣州", "Guangzhou", CN),
  SZX("深圳", "Shenzhen", CN),

  // ID
  CGK("雅加達", "Jakarta", ID),
  DPS("峇里島", "Bali", ID),

  // VN
  SGN("胡志明市", "Ho Chi Minh City", VN),
  HAN("河内", "Hanoi", VN),

  KTM("加德滿都", "Kathmandu", NP),

  // PK
  ISB("伊斯蘭堡", "Islamabad", PK),
  KHI("克拉嗤", "Karachi", PK),
  DAC("達卡", "Dhaka", BD),
  CMB("可倫坡", "Colombo", LK),
  TLV("特拉维夫", "Tel Aviv", IL),
  AMM("安曼", "Amman", JO),
  BEY("貝魯特", "Beirut", LB),
  DOH("多哈", "Doha", QA),
  AUH("阿布扎比", "Abu Dhabi", AE),
  MCT("馬斯開特", "Muscat", OM),
  BAH("巴林", "Manama", BH),
  KWI("科威特", "Kuwait City", KW),

  ;

  private String chineseName;
  private String englishName;
  private CountryCode countryCode;

  CityCode(String chineseName, String englishName, CountryCode countryCode) {
    this.chineseName = chineseName;
    this.englishName = englishName;
    this.countryCode = countryCode;
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
