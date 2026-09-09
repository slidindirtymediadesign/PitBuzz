<?php
require __DIR__.'/bootstrap.php';
$id=user_id(); if(!$id) out(['user'=>null]);
$s=db()->prepare("SELECT id,name,email,system_role AS systemRole FROM users WHERE id=?"); $s->execute([$id]); out(['user'=>$s->fetch()?:null]);
