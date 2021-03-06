/*
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 *   https://joinup.ec.europa.eu/software/page/eupl
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 */

package org.rutebanken.tiamat.model;

public enum IanaCountryTldEnumeration {


    AC("ac"),

    AD("ad"),

    AE("ae"),

    AF("af"),

    AG("ag"),

    AI("ai"),

    AL("al"),

    AM("am"),

    AN("an"),

    AO("ao"),

    AQ("aq"),

    AR("ar"),

    AS("as"),

    AT("at"),

    AU("au"),

    AW("aw"),

    AZ("az"),

    AX("ax"),

    BA("ba"),

    BB("bb"),

    BD("bd"),

    BE("be"),

    BF("bf"),

    BG("bg"),

    BH("bh"),

    BI("bi"),

    BJ("bj"),

    BM("bm"),

    BN("bn"),

    BO("bo"),

    BR("br"),

    BS("bs"),

    BT("bt"),

    BV("bv"),

    BW("bw"),

    BY("by"),

    BZ("bz"),

    CA("ca"),

    CC("cc"),

    CD("cd"),

    CF("cf"),

    CG("cg"),

    CH("ch"),

    CI("ci"),

    CK("ck"),

    CL("cl"),

    CM("cm"),

    CN("cn"),

    CO("co"),

    CR("cr"),

    CS("cs"),

    CU("cu"),

    CV("cv"),

    CX("cx"),

    CY("cy"),

    CZ("cz"),

    DE("de"),

    DJ("dj"),

    DK("dk"),

    DM("dm"),

    DO("do"),

    DZ("dz"),

    EC("ec"),

    EE("ee"),

    EG("eg"),

    EH("eh"),

    ER("er"),

    ES("es"),

    ET("et"),

    EU("eu"),

    FI("fi"),

    FJ("fj"),

    FK("fk"),

    FM("fm"),

    FO("fo"),

    FR("fr"),

    GA("ga"),

    GB("gb"),

    GD("gd"),

    GE("ge"),

    GF("gf"),

    GG("gg"),

    GH("gh"),

    GI("gi"),

    GL("gl"),

    GM("gm"),

    GN("gn"),

    GP("gp"),

    GQ("gq"),

    GR("gr"),

    GS("gs"),

    GT("gt"),

    GU("gu"),

    GW("gw"),

    GY("gy"),

    HK("hk"),

    HM("hm"),

    HN("hn"),

    HR("hr"),

    HT("ht"),

    HU("hu"),

    ID("id"),

    IE("ie"),

    IL("il"),

    IM("im"),

    IN("in"),

    IO("io"),

    IQ("iq"),

    IR("ir"),

    IS("is"),

    IT("it"),

    JE("je"),

    JM("jm"),

    JO("jo"),

    JP("jp"),

    KE("ke"),

    KG("kg"),

    KH("kh"),

    KI("ki"),

    KM("km"),

    KN("kn"),

    KP("kp"),

    KR("kr"),

    KW("kw"),

    KY("ky"),

    KZ("kz"),

    LA("la"),

    LB("lb"),

    LC("lc"),

    LI("li"),

    LK("lk"),

    LR("lr"),

    LS("ls"),

    LT("lt"),

    LU("lu"),

    LV("lv"),

    LY("ly"),

    MA("ma"),

    MC("mc"),

    MD("md"),

    MG("mg"),

    MH("mh"),

    MK("mk"),

    ML("ml"),

    MM("mm"),

    MN("mn"),

    MO("mo"),

    MP("mp"),

    MQ("mq"),

    MR("mr"),

    MS("ms"),

    MT("mt"),

    MU("mu"),

    MV("mv"),

    MW("mw"),

    MX("mx"),

    MY("my"),

    MZ("mz"),

    NA("na"),

    NC("nc"),

    NE("ne"),

    NF("nf"),

    NG("ng"),

    NI("ni"),

    NL("nl"),

    NO("no"),

    NP("np"),

    NR("nr"),

    NU("nu"),

    NZ("nz"),

    OM("om"),

    PA("pa"),

    PE("pe"),

    PF("pf"),

    PG("pg"),

    PH("ph"),

    PK("pk"),

    PL("pl"),

    PM("pm"),

    PN("pn"),

    PR("pr"),

    PS("ps"),

    PT("pt"),

    PW("pw"),

    PY("py"),

    QA("qa"),

    RE("re"),

    RO("ro"),

    RU("ru"),

    RW("rw"),

    SA("sa"),

    SB("sb"),

    SC("sc"),

    SD("sd"),

    SE("se"),

    SG("sg"),

    SH("sh"),

    SI("si"),

    SJ("sj"),

    SK("sk"),

    SL("sl"),

    SM("sm"),

    SN("sn"),

    SO("so"),

    SR("sr"),

    ST("st"),

    SV("sv"),

    SY("sy"),

    SZ("sz"),

    TC("tc"),

    TD("td"),

    TF("tf"),

    TG("tg"),

    TH("th"),

    TJ("tj"),

    TK("tk"),

    TL("tl"),

    TM("tm"),

    TN("tn"),

    TO("to"),

    TP("tp"),

    TR("tr"),

    TT("tt"),

    TV("tv"),

    TW("tw"),

    TZ("tz"),

    UA("ua"),

    UG("ug"),

    UK("uk"),

    UM("um"),

    US("us"),

    UY("uy"),

    UZ("uz"),

    VA("va"),

    VC("vc"),

    VE("ve"),

    VG("vg"),

    VI("vi"),

    VN("vn"),

    VU("vu"),

    WF("wf"),

    WS("ws"),

    YE("ye"),

    YT("yt"),

    YU("yu"),

    ZA("za"),

    ZM("zm"),

    ZW("zw");
    private final String value;

    IanaCountryTldEnumeration(String v) {
        value = v;
    }

    public static IanaCountryTldEnumeration fromValue(String v) {
        for (IanaCountryTldEnumeration c : IanaCountryTldEnumeration.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

    public String value() {
        return value;
    }

}
