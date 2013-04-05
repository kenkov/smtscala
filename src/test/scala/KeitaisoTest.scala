package jp.kenkov.smt.test
import org.scalatest.FunSuite
import scala.collection.mutable.{Map => MMap}
import jp.kenkov.smt.{_}

import jp.kenkov.smt.japanese.{Keitaiso}

class KeitaisoTest extends FunSuite {
  test("parseKeitaiso test 動く") {

    val k: Keitaiso = Keitaiso.parseKeitaiso("動く",
                                             "動詞,自立,*,*,五段・カ行イ音便,基本形,動く,ウゴク,ウゴク")
    val hyousoukei = "動く"
    val hinsi = "動詞"
    val hinsi1 = "自立"
    val hinsi2 = "*"
    val hinsi3 = "*"
    val katuyoukei = "五段・カ行イ音便"
    val katuyougata = "基本形"
    val genkei = "動く"
    val yomi = "ウゴク"
    val hatuon = "ウゴク"

    val exp: Keitaiso = new Keitaiso(hyousoukei, hinsi, hinsi1, hinsi2, hinsi3,
                                     katuyoukei, katuyougata, genkei, yomi, hatuon)
    expect(exp) { k }
    /*
    l = Keitaiso(u'1000\t名詞,数,*,*,*,*,*')
    assert l.chasen_data == u'1000\t名詞,数,*,*,*,*,*'
    assert l.hyousoukei == u'1000'
    assert l.hinsi == u'名詞'
    assert l.hinsi1 == u'数'
    assert l.hinsi2 == u'*'
    assert l.hinsi3 == u'*'
    assert l.katuyoukei == u'*'
    assert l.katuyougata == u'*'
    assert l.genkei == u'*'
    assert l.yomi == u'*'
    assert l.hatuon == u'*'
    */
  }
}
