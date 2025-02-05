<?php
include_once (__DIR__."/../persistencia/Conexion.php"); 
include_once (__DIR__."/../persistencia/TipoDAO.php"); 
class Tipo{
    private $id_tipo;
    private $descripcion;
    private $icon;

    public function setId_tipo($id_tipo){
        $this->id_tipo = $id_tipo;
    }
    
    public function getId_tipo(){
        return $this->id_tipo;
    }
    
    public function setDescripcion($descripcion){
        $this->descripcion = $descripcion;
    }
    
    public function getDescripcion(){
        return $this->descripcion;
    }
    
    public function setIcon($icon){
        $this->icon = $icon;
    }
    
    public function getIcon(){
        return $this->icon;
    }

    public function __construct($id_tipo=0, $descripcion="", $icon=""){
        $this->id_tipo = $id_tipo;
        $this->descripcion = $descripcion;
        $this->icon = $icon;
    }


    public function consultar(){
        $conexion = new Conexion();
        $conexion->abrirConexion();
        $tipoDAO = new TipoDAO($this->id_tipo);
        $conexion->ejecutarConsulta($tipoDAO->consultar());
        if($resultado = $conexion->siguienteRegistro()){
            $this->descripcion = $resultado[0];
            $this->icon = $resultado[1];
            $conexion->cerrarConexion();
            return true;
        }
        $conexion->cerrarConexion();
        return false;
    }
}
?>