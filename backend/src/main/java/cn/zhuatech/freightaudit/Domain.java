/* 上海如静知华信息科技有限公司 https://www.zhuatech.cn/ */
package cn.zhuatech.freightaudit;
import org.springframework.stereotype.Component;
import java.util.*;
import java.math.*;
import java.time.*;
import java.time.temporal.ChronoUnit;
import static cn.zhuatech.freightaudit.Model.*;
import static cn.zhuatech.freightaudit.Engine.*;
@Component public class Domain {
 static Map<String,Object> copy(Row r){return new LinkedHashMap<>(r.data());}
 static BigDecimal n(Row r,String k){return num(r.data(),k);}
 static BigDecimal z(Map<String,Object>d,String k){return d.containsKey(k)?num(d,k):BigDecimal.ZERO;}
 static String t(Row r,String k){return txt(r.data(),k);}
 static List<Row> linked(Engine e,User u,String module,String key,String id){return e.all(u,module).stream().filter(r->t(r,key).equals(id)).toList();}
 static void unique(Engine e,User u,String module,Map<String,Object>d,String key){require(e.all(u,module).stream().noneMatch(r->t(r,key).equalsIgnoreCase(txt(d,key))),"重复的"+key);}
 static void dates(Map<String,Object>d,String from,String to){require(!date(d,to).isBefore(date(d,from)),"结束日期不能早于开始日期");}
 static void change(Engine e,User u,Row row,String state,Map<String,Object>d,String note){e.save(u,row,state,d,"LINKED",note);}
 public void edit(Engine e,User u,Row r,Map<String,Object>d){
  if(r.module().equals("readings")){require(e.ref(u,r.data(),"job","jobs").state().equals("RUNNING")&&txt(d,"job").equals(t(r,"job")),"仅进行中的任务可以修改测量值，且不得迁移任务");require(linked(e,u,"readings","job",t(r,"job")).stream().noneMatch(x->!x.id().equals(r.id())&&t(x,"point").equals(txt(d,"point"))),"测量点编号重复");return;}
  if(r.module().equals("versions")){require(e.ref(u,r.data(),"artwork","artworks").state().equals("DRAFT"),"已送审稿件不可修改");require(txt(d,"artwork").equals(t(r,"artwork"))&&num(d,"revision").compareTo(n(r,"revision"))==0,"版本不能迁移任务或改写版本号");return;}
  for(var m:e.spec().modules())for(Row other:e.all(u,m.key()))if(!other.id().equals(r.id())&&other.data().values().stream().anyMatch(v->r.id().equals(v)))throw new Failure(409,"资料已有下游引用，请新建版本而不是改写历史");
  var fields=e.spec().module(r.module()).fields().stream().map(Field::key).toList();
  r.data().forEach((k,v)->{if(!fields.contains(k))d.put(k,v);});
  if(d.containsKey("start")&&d.containsKey("end"))dates(d,"start","end");
  if(d.containsKey("from")&&d.containsKey("to"))dates(d,"from","to");
  for(String key:List.of("serial","sku","invoice","invoiceNo","lockNo"))if(d.containsKey(key))require(e.all(u,r.module()).stream().noneMatch(x->!x.id().equals(r.id())&&t(x,key).equalsIgnoreCase(txt(d,key))),"重复唯一业务标识: "+key);
  if(d.containsKey("bonusRate"))require(num(d,"bonusRate").compareTo(num(d,"baseRate"))>=0,"达档返利率不能低于基础返利率");
  if(d.containsKey("lifeLimit"))require(num(d,"serviceEvery").compareTo(num(d,"lifeLimit"))<=0,"保养间隔不能大于寿命");
  if(d.containsKey("defects"))require(num(d,"defects").compareTo(num(d,"shots"))<=0,"不良数不能超过生产次数");
  if(d.containsKey("nps"))require(num(d,"nps").compareTo(BigDecimal.TEN)<=0&&num(d,"csat").compareTo(new BigDecimal("5"))<=0,"评价分数超出范围");
  if(d.containsKey("oxygenMin"))require(num(d,"oxygenMin").compareTo(num(d,"oxygenMax"))<0,"氧气下限须小于上限");
  if(r.module().equals("invoices"))require(e.all(u,"invoices").stream().noneMatch(x->!x.id().equals(r.id())&&t(x,"shipment").equals(txt(d,"shipment"))),"运单已关联结算账单");
  if(r.module().equals("sales")){Row program=e.ref(u,d,"program","programs");require(program.state().equals("ACTIVE")&&!date(d,"soldAt").isBefore(date(program.data(),"start"))&&!date(d,"soldAt").isAfter(date(program.data(),"end")),"协议状态或销售日期无效");}
  if(r.module().equals("jobs")){Row instrument=e.ref(u,d,"instrument","instruments"),standard=e.ref(u,d,"standard","standards");require(!instrument.state().equals("RETIRED")&&t(instrument,"unit").equals(t(standard,"unit")),"器具状态或计量单位无效");require(!date(d,"performedAt").isAfter(LocalDate.now()),"不能记录未来校准");}
  if(r.module().equals("permits")){require(ChronoUnit.DAYS.between(date(d,"start"),date(d,"end"))<=7,"许可最长七天");require(t(e.ref(u,d,"isolation","isolations"),"location").equals(txt(d,"location")),"隔离区域不匹配");}
  if(r.module().equals("responses")){require(e.all(u,"responses").stream().noneMatch(x->!x.id().equals(r.id())&&t(x,"survey").equals(txt(d,"survey"))&&t(x,"customer").equals(txt(d,"customer"))),"客户已存在该问卷反馈");require(t(e.ref(u,d,"customer","customers"),"consent").equals("YES"),"客户未允许反馈邀请");}
  if(r.module().equals("products")){String barcode=txt(d,"barcode");require(barcode.matches("\\d{13}"),"条码须为 EAN-13");int sum=0;for(int x=0;x<12;x++)sum+=(barcode.charAt(x)-'0')*(x%2==0?1:3);require((10-sum%10)%10==barcode.charAt(12)-'0',"EAN-13 校验位不正确");}

 }
 public Map<String,Object> metrics(Engine e,User u){
  var out=new LinkedHashMap<String,Object>();out.put("待审账单",e.all(u,"invoices").stream().filter(r->r.state().equals("DRAFT")).count());out.put("争议金额",e.all(u,"invoices").stream().filter(r->r.state().equals("DISPUTED")).map(r->n(r,"difference")).reduce(BigDecimal.ZERO,BigDecimal::add));out.put("已核销运费",e.all(u,"payments").stream().map(r->n(r,"amount")).reduce(BigDecimal.ZERO,BigDecimal::add));;return out;
 }
 public void create(Engine e,User u,String module,Map<String,Object>d){switch(module){case "tariffs" -> dates(d,"start","end");case "invoices" -> {unique(e,u,module,d,"invoiceNo");require(e.all(u,module).stream().noneMatch(r->t(r,"shipment").equals(txt(d,"shipment"))),"一张运单只能对应一份结算账单");} default -> {} }}
 public String action(Engine e,User u,Row r,String action,Map<String,Object>i,Map<String,Object>d){
  String k=r.module()+"."+action;switch(k){
case "tariffs.approve" -> require(e.all(u,"tariffs").stream().noneMatch(t->!t.id().equals(r.id())&&t.state().equals("ACTIVE")&&t(t,"carrier").equals(txt(d,"carrier"))&&t(t,"lane").equals(txt(d,"lane"))&&!date(t.data(),"end").isBefore(date(d,"start"))&&!date(t.data(),"start").isAfter(date(d,"end"))),"同承运商线路的价卡生效区间重叠");
case "shipments.deliver" -> d.putAll(i);
case "invoices.audit" -> {
 Row shipment=e.ref(u,d,"shipment","shipments");require(shipment.state().equals("DELIVERED"),"运单未妥投，禁止应付结算");
 List<Row> tariffs=e.all(u,"tariffs").stream().filter(t->t.state().equals("ACTIVE")&&t(t,"carrier").equals(t(shipment,"carrier"))&&t(t,"lane").equals(t(shipment,"lane"))&&!date(shipment.data(),"shippedAt").isBefore(date(t.data(),"start"))&&!date(shipment.data(),"shippedAt").isAfter(date(t.data(),"end"))).toList();
 require(tariffs.size()==1,"没有唯一适用价卡");Row tariff=tariffs.getFirst();BigDecimal expected=money(n(tariff,"base").add(n(tariff,"perKg").multiply(n(shipment,"weight"))).multiply(BigDecimal.ONE.add(n(tariff,"fuelRate").divide(new BigDecimal("100")))));
 d.put("expected",expected);d.put("tolerance",n(tariff,"tolerance"));d.put("tariff",tariff.id());d.put("tariffSnapshot",tariff.data());d.put("difference",z(d,"claimed").subtract(expected));d.put("payable",z(d,"claimed"));
 return z(d,"difference").abs().compareTo(n(tariff,"tolerance"))>0?"DISPUTED":"MATCHED";
}
case "invoices.adjust" -> {require(z(d,"difference").signum()>0,"低报账单须更正源账单，不能通过贷项增加金额");BigDecimal amount=z(d,"claimed").subtract(num(i,"credit"));require(amount.signum()>0&&amount.subtract(z(d,"expected")).abs().compareTo(z(d,"tolerance"))<=0,"贷项后金额仍超差或无效");d.putAll(i);d.put("payable",amount);d.put("difference",amount.subtract(z(d,"expected")));}
case "invoices.pay" -> {require(e.all(u,"payments").stream().noneMatch(p->t(p,"reference").equals(txt(i,"reference"))),"付款凭证重复");d.putAll(i);e.ledger(u,"payments","POSTED",Map.of("invoice",r.id(),"amount",z(d,"payable"),"reference",txt(i,"reference")));}
case "invoices.correct" -> {d.put("claimed",num(i,"claimed"));d.put("correctionReason",txt(i,"reason"));}
 default -> {} }return null;
 }
}
