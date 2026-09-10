package app.pitbuzz.team.ui
data class ValidationResult(val ok:Boolean,val message:String="")
object RegistrationValidator{
 fun normalizePhone(v:String):String{val d=v.filter{it.isDigit()};return if(d.length==11&&d.startsWith("1"))d.drop(1) else d}
 fun validate(name:String,p:String,cp:String,e:String,pw:String,cpw:String):ValidationResult{
  if(name.trim().isEmpty())return ValidationResult(false,"Name is required")
  val a=normalizePhone(p);if(a.length!=10)return ValidationResult(false,"Enter a valid 10-digit phone number")
  if(a!=normalizePhone(cp))return ValidationResult(false,"Phone numbers do not match")
  if(!e.contains("@")||!e.contains("."))return ValidationResult(false,"Enter a valid email")
  if(pw.length<8)return ValidationResult(false,"Password must be at least 8 characters")
  if(pw!=cpw)return ValidationResult(false,"Passwords do not match")
  return ValidationResult(true)
 }
}
