<?php
class ArticuloDAO{
    private $id_articulo;
    private $descripcion;
    private $cantidad;
    private $fecha_ingreso;

    private $id_instalacion;

    public function __construct($id_articulo=0, $descripcion="", $cantidad=0, $fecha_ingreso="", $id_instalacion=0) {
        $this->id_articulo = $id_articulo;
        $this->descripcion = $descripcion;
        $this->cantidad = $cantidad;
        $this->fecha_ingreso = $fecha_ingreso;
        $this->id_instalacion = $id_instalacion;
    }

    public function consultarArticulosInternet(){ // 55, 56, 88, 83, 72
        return"SELECT id_articulos, nombre_articulo
               FROM articulos
               WHERE id_articulos IN (55, 56, 88, 83, 72)
        ";
    }

    public function consultarArticulosInternetTV(){ // 55, 56, 160, 186, 88, 83, 72, 108, 109, 110, 111
        return"SELECT id_articulos, nombre_articulo
               FROM articulos
               WHERE id_articulos IN (55, 56, 160, 186, 88, 83, 72, 109, 110, 111)
                ";
    }
    
    public function consultarArticulosInternetTVTelefonia(){// 55, 56, 160, 186, 88, 83, 72, 108, 109, 110, 111
        return"SELECT id_articulos, nombre_articulo
               FROM articulos
               WHERE id_articulos IN (55, 56, 160, 186, 88, 83, 72, 109, 110, 111)
                ";
    }
    
    public function consultarArticulosInternetTelefonia(){ // 55, 56, 88, 83, 72
        return"SELECT id_articulos, nombre_articulo
               FROM articulos
               WHERE id_articulos IN (55, 56, 88, 83, 72)
                ";
    }
    
    public function consultarArticulosTV(){// 55, 160, 186, 88, 83, 72, 
        return"SELECT id_articulos, nombre_articulo
               FROM articulos
               WHERE id_articulos IN (55, 160, 186, 88, 83, 72, 109, 110, 111)
        ";
    }

    public function guardarArticulo(){
        return "INSERT INTO instalacion_articulo (id_instalacion, cantidad, fecha_salida, id_articulo )
                VALUES (".$this->id_instalacion.", ".$this->cantidad.", '".$this->fecha_ingreso."', ".$this->id_articulo.")
        ";
    }
}

?>