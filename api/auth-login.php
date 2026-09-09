<?php
require __DIR__.'/bootstrap.php';
if($_SERVER['REQUEST_METHOD']!=='POST') out(['error'=>'Method not allowed'],405);
$b=body(); $s=db()->prepare("SELECT id,name,email,password_hash,system_role FROM users WHERE email=? LIMIT 1"); $s->execute([strtolower(trim($b['email']??''))]); $u=$s->fetch();
if(!$u||!password_verify($b['password']??'',$u['password_hash'])) out(['error'=>'Invalid email or password'],401);
session_regenerate_id(true); $_SESSION['user_id']=(int)$u['id']; unset($u['password_hash']); out(['ok'=>true,'user'=>$u]);
