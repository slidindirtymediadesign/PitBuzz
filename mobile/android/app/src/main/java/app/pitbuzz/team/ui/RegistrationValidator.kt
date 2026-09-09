package app.pitbuzz.team.ui
object RegistrationValidator{fun phone(v:String):String{val d=v.filter{it.isDigit()};return if(d.length==11&&d.startsWith("1"))d.drop(1) else d};fun validate(p:String,cp:String,e:String,pw:String,cpw:String)=phone(p).length==10&&phone(p)==phone(cp)&&e.contains("@")&&pw.length>=8&&pw==cpw}
