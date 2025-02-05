<?php
include_once(__DIR__."/../persistencia/Conexion.php");
include_once(__DIR__.'/../persistencia/ArticuloDAO.php');
class Articulo{
    private $id_articulo;
    private $descripcion;
    private $cantidad;
    private $fecha_ingreso;
    private $id_instalacion;
    
    public function getId_articulo() {
        return $this->id_articulo;
    }
    
    public function getDescripcion() {
        return $this->descripcion;
    }
    
    public function getCantidad() {
        return $this->cantidad;
    }
    
    public function getFecha_ingreso() {
        return $this->fecha_ingreso;
    }
    
    public function setId_articulo($id_articulo) {
        $this->id_articulo = $id_articulo;
    }
    
    public function setDescripcion($descripcion) {
        $this->descripcion = $descripcion;
    }
    
    public function setCantidad($cantidad) {
        $this->cantidad = $cantidad;
    }
    
    public function setFecha_ingreso($fecha_ingreso) {
        $this->fecha_ingreso = $fecha_ingreso;
    }

    public function __construct($id_articulo=0, $descripcion="", $cantidad=0, $fecha_ingreso="", $id_instalacion=0) {
        $this->id_articulo = $id_articulo;
        $this->descripcion = $descripcion;
        $this->cantidad = $cantidad;
        $this->fecha_ingreso = $fecha_ingreso;
        $this->id_instalacion = $id_instalacion;
    }

    public function consultarPorTipo($tipo){
        $conexion = new Conexion();
        $conexion->abrirConexion();
        $articuloDAO = new ArticuloDAO();
        $consulta = "";
        if($tipo == 1){
            $consulta = $articuloDAO->consultarArticulosInternet();
        }else if($tipo == 2){
            $consulta = $articuloDAO->consultarArticulosInternetTV();
        }else if($tipo == 3){
            $consulta = $articuloDAO->consultarArticulosInternetTVTelefonia();
        }else if($tipo == 4){
            $consulta = $articuloDAO->consultarArticulosInternetTelefonia();
        }else if($tipo == 5){
            $consulta = $articuloDAO->consultarArticulosTV();
        }
        $conexion->ejecutarConsulta($consulta);
        $articulos = array();
        while($registro = $conexion->siguienteRegistro()){
            $articulo = new Articulo($registro[0], $registro[1], 0, "");
            array_push($articulos, $articulo);
        }
        $conexion->cerrarConexion();
        return $articulos;
    }

    public function guardarArticulo(){
        $conexion = new Conexion();
        $conexion->abrirConexion();
        $articuloDAO = new ArticuloDAO($this->id_articulo, null, $this->cantidad, $this->fecha_ingreso, $this->id_instalacion);
        echo $articuloDAO->guardarArticulo();
        $conexion->ejecutarConsulta($articuloDAO->guardarArticulo());
        $conexion->cerrarConexion();
    }
}
?>
