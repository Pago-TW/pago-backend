package tw.pago.pagobackend.constant;

import static tw.pago.pagobackend.constant.CountryCode.*;

import lombok.Getter;

@Getter
public enum CityCode {
  // ANY
  ANY("任何城市", "Any City", null),

  // TW
  TPE("台北市", "Taipei", TW),
  TPH("新北市", "New Taipei", TW),
  TYC("桃園市", "Taoyuan", TW),
  TXG("台中市", "Taichung", TW),
  TNN("台南市", "Tainan", TW),
  KHH("高雄市", "Kaohsiung", TW),
  KLU("基隆市", "Keelung", TW),
  ILC("宜蘭縣", "Yilan County", TW),
  HSH("新竹縣", "Hsinchu County", TW),
  MAL("苗栗縣", "Miaoli County", TW),
  CWH("彰化縣", "Changhua County", TW),
  NTO("南投縣", "Nantou County", TW),
  YLH("雲林縣", "Yunlin County", TW),
  CHY("嘉義縣", "Yunlin County", TW),
  HWA("花蓮縣", "Hualien County", TW),
  TTT("台東縣", "Taitung County", TW),
  CYI("嘉義市", "Chiayi", TW),
  IUH("屏東縣", "Pingtung County", TW),
  PEH("澎湖縣", "Penghu County", TW),


  // JP
  TKO("東京市", "Tokyo", JP),
  NRT("東京成田", "Tokyo Narita", JP),
  HND("東京羽田", "Tokyo International Airport", JP),
  NGO("名古屋", "Nagoya", JP),
  CTS("札幌市", "Sapporo", JP),
  KIX("大阪市", "Osaka", JP),
  FUK("福岡市", "Fukuoka", JP),
  OKA("沖繩", "Okinawa", JP),
  HKK("北海道", "Hokkaido", JP),
  YKH("橫濱", "Yokohama", JP),
  KYO("京都", "Kyoto", JP),
  NAR("奈良", "Nara", JP),
  SPK("札幌", "Sapporo", JP),
  HIJ("廣島市", "Hiroshima", JP),
  KUM("熊本市", "Kumamoto", JP),

  // KR
  SEL("首爾", "Seoul", KR),
  PUS("釜山", "Busan", KR),
  ICN("仁川", "Incheon", KR),
  CJU("濟州", "Jeju", KR),
  TAE("大邱", "Daegu", KR),
  KWJ("光州", "Gwangju", KR),


  // HK
  HKG("香港", "Hong Kong", HK),

  // TH
  BKK("曼谷", "Bangkok", TH),
  HKT("普吉島", "Phuket", TH),
  CNX("清邁", "Chiang Mai", TH),
  KBV("甲米", "Krabi", TH),
  USM("蘇美島", "Ko Samui", TH),

  // VN
  SGN("胡志明市", "Ho Chi Minh City", VN),
  HAN("河内", "Hanoi", VN),

  // IN
  BOM("孟買", "Mumbai", IN),
  DEL("德里", "Delhi", IN),
  MAA("金奈", "Chennai", IN),
  BLR("邦加羅爾", "Bengaluru", IN),

  // SG
  SIN("新加坡", "Singapore", SG),

  // MY
  KUL("吉隆坡", "Kuala Lumpur", MY),
  PEN("檳城", "Penang", MY),
  JHB("新山", "Johor Bahru", MY),
  KCH("古晉", "Kuching", MY),
  BKI("亞庇", "Kota Kinabalu", MY),

  // PH
  MNL("馬尼拉", "Manila", PH),
  CEB("宿霧", "Cebu", PH),
  BCD("馬卡薩", "Bacolod", PH),
  DVO("達沃", "Davao", PH),
  PPS("普林塞薩", "Puerto Princesa", PH),


  // ID
  CGK("雅加達", "Jakarta", ID),
  DPS("峇里島", "Bali", ID),

  // US
  JFK("紐約", "New York", US),
  LAX("洛杉磯", "Los Angeles", US),
  ORD("芝加哥", "Chicago", US),
  IAH("休士頓", "Houston", US),
  PHX("鳳凰城", "Phoenix", US),
  PHL("費城", "Philadelphia", US),
  SAT("聖安東尼奧", "San Antonio", US),
  SAN("聖地牙哥", "San Diego", US),
  DFW("達拉斯", "Dallas", US),
  SJC("聖荷西", "San Jose", US),
  AUS("奧斯汀", "Austin", US),
  JAX("傑克孫維", "Jacksonville", US),
  CLT("夏洛特", "Charlotte", US),
  SFO("舊金山", "San Francisco", US),
  SEA("西雅圖", "Seattle", US),
  DCA("華盛頓哥倫比亞特區", "Washington D.C.", US),
  BNA("納許維爾", "Nashville", US),
  BOS("波士頓", "Boston", US),
  LAS("拉斯維加斯", "Las Vegas", US),
  DTW("底特律", "Detroit", US),
  SDF("路易維爾", "Louisville", US),
  BWI("巴爾的摩", "Baltimore", US),
  MCI("堪薩斯城", "Kansas City", US),
  ATL("亞特蘭大", "Atlanta", US),
  LGB("長灘", "Long Beach", US),
  MIA("邁阿密", "Miami", US),
  MSY("紐奧良", "New Orleans", US),
  HNL("檀香山", "Honolulu", US),
  STP("聖保羅", "Saint Paul", US),
  LNK("林肯", "Lincoln", US),
  BUF("水牛城", "Buffalo", US),

  // CA
  YYZ("多倫多", "Toronto", CA),
  YUL("蒙特利爾", "Montreal", CA),
  YVR("溫哥華", "Vancouver", CA),
  YYC("卡爾加里", "Calgary", CA),
  YEG("埃德蒙頓", "Edmonton", CA),
  YOW("渥太華", "Ottawa", CA),
  YHZ("哈利法克斯", "Halifax", CA),
  YWG("溫尼伯", "Winnipeg", CA),
  YYT("聖約翰斯", "St. John's", CA),
  YQB("魁北克城", "Quebec City", CA),
  YLW("基洛納", "Kelowna", CA),
  YXE("薩斯卡通", "Saskatoon", CA),
  YQR("里賈納", "Regina", CA),
  YMM("福特麥默里", "Fort McMurray", CA),
  YQT("珊德灣", "Thunder Bay", CA),

  // MX
  MEX("墨西哥城", "Mexico City", MX),
  CUN("坎昆", "Cancún", MX),
  GDL("瓜達拉哈拉", "Guadalajara", MX),
  MTY("蒙特雷", "Monterrey", MX),
  PVR("普埃爾托瓦亞塔", "Puerto Vallarta", MX),

  // BR
  GRU("聖保羅", "São Paulo", BR),
  GIG("里約熱內盧", "Rio de Janeiro", BR),
  BSB("巴西利亞", "Brasília", BR),
  SSA("薩爾瓦多", "Salvador", BR),
  CWB("庫里奇巴", "Curitiba", BR),

  // AU
  SYD("悉尼", "Sydney", AU),
  MEL("墨爾本", "Melbourne", AU),
  BNE("布里斯班", "Brisbane", AU),
  PER("珀斯", "Perth", AU),
  ADL("阿德萊德", "Adelaide", AU),
  CNS("凱恩斯", "Cairns", AU),
  OOL("黃金海岸", "Gold Coast", AU),

  // FR
  CDG("巴黎", "Paris", FR),
  MRS("馬賽", "Marseille", FR),
  LYS("里昂", "Lyon", FR),
  TLS("土魯斯", "Toulouse", FR),
  NCE("尼斯", "Nice", FR),
  NTE("南特", "Nantes", FR),
  BOD("波爾多", "Bordeaux", FR),
  SXB("斯特拉斯堡", "Strasbourg", FR),
  URO("盧昂", "Rouen", FR),
  GNB("格勒諾布爾", "Grenoble", FR),
  TLN("土倫", "Toulon", FR),
  RNS("雷恩", "Rennes", FR),


  // GB
  LHR("倫敦", "London", GB),
  MAN("曼徹斯特", "Manchester", GB),
  EDI("愛丁堡", "Edinburgh", GB),
  BHX("伯明翰", "Birmingham", GB),
  LPL("利物浦", "Liverpool", GB),
  NCL("紐卡斯爾", "Newcastle", GB),
  BRS("布里斯托", "Bristol", GB),
  ABZ("阿伯丁", "Aberdeen", GB),
  LDS("利茲", "Leeds", GB),
  SOU("南安普頓", "Southampton", GB),
  STN("史丹斯特德", "Stansted", GB),
  BFS("貝爾法斯特", "Belfast", GB),
  EMA("東密德蘭", "East Midlands", GB),
  LBA("布拉德福德", "Bradford", GB),
  PIK("普雷斯維克", "Prestwick", GB),
  CWL("卡迪夫", "Cardiff", GB),
  LTN("盧頓", "Luton", GB),
  GLA("格拉斯哥", "Glasgow", GB),
  INV("因弗內斯", "Inverness", GB),
  DND("鄧迪", "Dundee", GB),
  BHD("喬治·貝斯特·貝爾法斯特城市", "George Best Belfast City", GB),
  BOH("伯恩茅斯", "Bournemouth", GB),
  DSA("唐卡斯特", "Doncaster", GB),
  NWI("諾里奇", "Norwich", GB),

  // DE
  FRA("法蘭克福", "Frankfurt", DE),
  MUC("慕尼黑", "Munich", DE),
  TXL("柏林", "Berlin", DE),

  // IT
  FCO("羅馬", "Rome", IT),
  MXP("米蘭", "Milan", IT),
  VCE("威尼斯", "Venice", IT),

  // ES
  MAD("馬德里", "Madrid", ES),
  BCN("巴塞羅那", "Barcelona", ES),
  AGP("馬拉加", "Malaga", ES),

  // NL
  AMS("阿姆斯特丹", "Amsterdam", NL),
  RTM("鹿特丹", "Rotterdam", NL),

  // BE
  BRU("布魯塞爾", "Brussels", BE),
  CRL("沙勒羅瓦", "Charleroi", BE),

  // PT
  LIS("里斯本", "Lisbon", PT),
  OPO("波爾圖", "Porto", PT),

  // CH
  ZRH("蘇黎世", "Zurich", CH),
  GVA("日內瓦", "Geneva", CH),



  // TR
  IST("伊斯坦堡", "Istanbul", TR),
  AYT("安塔利亞", "Antalya", TR),
  ADB("伊茲密爾", "Izmir", TR),
  ESB("安卡拉", "Ankara", TR),
  DLM("達拉曼", "Dalaman", TR),


  // RU
  SVO("莫斯科", "Moscow", RU),
  LED("聖彼得堡", "St. Petersburg", RU),
  KZN("喀山", "Kazan", RU),


  // NP
  KTM("加德滿都", "Kathmandu", NP),

  // AE
  DXB("杜拜", "Dubai", AE),
  AUH("阿布達比", "Abu Dhabi", AE),
  SHJ("沙迦", "Sharjah", AE),
  AAN("艾因", "Al Ain", AE),

  // PK
  ISB("伊斯蘭堡", "Islamabad", PK),
  KHI("克拉嗤", "Karachi", PK),

  // BD
  DAC("達卡", "Dhaka", BD),

  // LK
  CMB("可倫坡", "Colombo", LK),

  // IL
  TLV("特拉维夫", "Tel Aviv", IL),

  // JO
  AMM("安曼", "Amman", JO),

  // LB
  BEY("貝魯特", "Beirut", LB),

  // QA
  DOH("多哈", "Doha", QA),

  // OM
  MCT("馬斯開特", "Muscat", OM),

  // BH
  BAH("巴林", "Manama", BH),

  // KW
  KWI("科威特", "Kuwait City", KW),

  // CN
  PEK("北京", "Beijing", CN),
  PVG("上海", "Shanghai", CN),
  CAN("廣州", "Guangzhou", CN),
  SZX("深圳", "Shenzhen", CN),
  CTU("成都", "Chengdu", CN),
  HGH("杭州", "Hangzhou", CN),
  CSX("長沙", "Changsha", CN),
  CKG("重慶", "Chongqing", CN),
  SHE("瀋陽", "Shenyang", CN),
  DLC("大連", "Dalian", CN),
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
