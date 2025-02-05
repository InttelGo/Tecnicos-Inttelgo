<?php 
include_once (__DIR__."/../persistencia/Conexion.php"); 
include_once (__DIR__."/../persistencia/BarrioDAO.php"); 
class Barrio{
    private $id_barrio;
    private $nombre;
    private $prefijo;

    public function setId_barrio($id_barrio){
        $this->id_barrio = $id_barrio;
    }
    
    public function getId_barrio(){
        return $this->id_barrio;
    }
    
    public function setNombre($nombre){
        $this->nombre = $nombre;
    }
    
    public function getNombre(){
        return $this->nombre;
    }
    
    public function setPrefijo($prefijo){
        $this->prefijo = $prefijo;
    }
    
    public function getPrefijo(){
        return $this->prefijo;
    }

    public function __construct($id_barrio=0, $nombre="", $prefijo=""){
        $this->id_barrio = $id_barrio;
        $this->nombre = $nombre;
        $this->prefijo = $prefijo;
    }
    
    public function consultarTodosPrefijos(){
        $barrios = array();
        $conexion = new Conexion();
        $conexion->abrirConexion();
        $barrioDAO = new BarrioDAO();
        $conexion->ejecutarConsulta($barrioDAO->consultarTodosPrefijos());
        while($resultado = $conexion->siguienteRegistro()){
            $barrio = new Barrio(null, null, $resultado[0]);
            array_push($barrios, $barrio);
        }
        $conexion->cerrarConexion();
        return $barrios;
    }

}
?>