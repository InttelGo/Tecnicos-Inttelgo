<?php 
$barrio = new Barrio();
$barrios = $barrio->consultarTodosPrefijos();

echo json_encode([
    "success" => true,
    "barrios" => array_map(function($b) {
        return [
            "prefijo" => $b->getPrefijo()
        ];
    }, $barrios)
], JSON_PRETTY_PRINT | JSON_UNESCAPED_UNICODE);
?>