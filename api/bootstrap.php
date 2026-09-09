<?php
declare(strict_types=1);
header('Content-Type: application/json; charset=utf-8');
header('Cache-Control: no-store');
session_name('teampitbuzz');
session_set_cookie_params(['httponly'=>true,'secure'=>true,'samesite'=>'Lax','path'=>'/']);
if(session_status()!==PHP_SESSION_ACTIVE) session_start();

function db(): PDO {
  static $pdo=null;
  if($pdo) return $pdo;
  $dsn='mysql:host='.(getenv('DB_HOST')?:'localhost').';port='.(getenv('DB_PORT')?:'3306').';dbname='.getenv('DB_NAME').';charset=utf8mb4';
  return $pdo=new PDO($dsn,getenv('DB_USER'),getenv('DB_PASSWORD')?:'',[
    PDO::ATTR_ERRMODE=>PDO::ERRMODE_EXCEPTION,
    PDO::ATTR_DEFAULT_FETCH_MODE=>PDO::FETCH_ASSOC,
    PDO::ATTR_EMULATE_PREPARES=>false
  ]);
}
function body(): array { return json_decode(file_get_contents('php://input'),true) ?: []; }
function out($data,int $status=200): never { http_response_code($status); echo json_encode($data); exit; }
function user_id(): ?int { return isset($_SESSION['user_id'])?(int)$_SESSION['user_id']:null; }
function require_user(): int { $id=user_id(); if(!$id) out(['error'=>'Not authenticated'],401); return $id; }
function require_race_admin(): int {
  $id=require_user();
  $s=db()->prepare("SELECT system_role FROM users WHERE id=?"); $s->execute([$id]);
  $r=$s->fetchColumn(); if(!in_array($r,['race_admin','super_admin'],true)) out(['error'=>'Race admin required'],403);
  return $id;
}
