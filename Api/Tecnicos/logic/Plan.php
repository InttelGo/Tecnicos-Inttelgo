<?php 
include_once (__DIR__."/../persistencia/Conexion.php"); 
include_once (__DIR__."/../persistencia/PlanDAO.php");
class Plan{
    private $id_plan;
    private $descripcion;
    public function setId_plan($id_plan){
        $this->id_plan = $id_plan;
    }
    
    public function getId_plan(){
        return $this->id_plan;
    }
    
    public function setDescripcion($descripcion){
        $this->descripcion = $descripcion;
    }
    
    public function getDescripcion(){
        return $this->descripcion;
    }
    
    public function __construct( $id_plan=0, $descripcion=""){
        $this->id_plan = $id_plan;
        $this->descripcion = $descripcion;
    }
} 
?>