<?php
if(isset($_GET["type"])){
    $articulo = new Articulo();
    $articulos = $articulo->consultarPorTipo($_GET["type"]);
    echo json_encode([
        "success" => true,
        "articulos" => array_map(function($a) {
            return [
                "id_articulo" => $a->getId_articulo(),
                "descripcion" => $a->getDescripcion(),
                "cantidad" => $a->getCantidad(),
                "fecha_ingreso" => $a->getFecha_ingreso()
            ];
        }, $articulos)
    ], JSON_PRETTY_PRINT | JSON_UNESCAPED_UNICODE);
}
?>