<?php 
include_once(__DIR__."/../persistencia/Conexion.php");
include_once(__DIR__.'/../persistencia/ObservacionDAO.php');
class Observacion{
    private $id_instalacion;
    private $id;
    private $descripcion;

    public function getId_instalacion(){
        return $this->id_instalacion;
    }
    public function getId(){
        return $this->id;
    }
    public function getDescripcion(){
        return $this->descripcion;
    }
    public function setId_instalacion($id_instalacion){
        $this->id_instalacion = $id_instalacion;
    }
    public function setId($id){
        $this->id = $id;
    }
    public function setDescripcion($descripcion){
        $this->descripcion = $descripcion;
    }

    public function __construct($id_instalacion =0, $id=0, $descripcion=""){
        $this->id_instalacion = $id_instalacion;
        $this->id = $id;
        $this->descripcion = $descripcion;
    }

    public function consultarPorUltimoId(){
        $conexion = new Conexion();
        $conexion ->abrirConexion();
        $observacionDAO = new ObservacionDAO($this->id_instalacion);
        $conexion->ejecutarConsulta($observacionDAO->consultarPorUltimoID());
        $registro = $conexion->siguienteRegistro();
        $this->id = $registro[0];
        $this->descripcion = $registro[1];
        $conexion->cerrarConexion();
    }
}
?>