<?php
require __DIR__.'/bootstrap.php';
if($_SERVER['REQUEST_METHOD']!=='POST') out(['error'=>'Method not allowed'],405);
$b=body(); $email=strtolower(trim($b['email']??'')); $name=trim($b['name']??''); $password=$b['password']??'';
if(!filter_var($email,FILTER_VALIDATE_EMAIL)||$name===''||strlen($password)<8) out(['error'=>'Name, valid email and 8+ character password required'],422);
try {
 $s=db()->prepare("INSERT INTO users(email,password_hash,name) VALUES(?,?,?)");
 $s->execute([$email,password_hash($password,PASSWORD_DEFAULT),$name]);
 $_SESSION['user_id']=(int)db()->lastInsertId(); out(['ok'=>true,'user'=>['id'=>$_SESSION['user_id'],'name'=>$name,'email'=>$email]],201);
} catch(PDOException $e){ if($e->getCode()==='23000') out(['error'=>'Email already registered'],409); throw $e; }
