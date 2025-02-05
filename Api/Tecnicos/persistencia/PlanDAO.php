<?php 
class PlanDAO{
    private $id_plan;
    private $descripcion;
    private $id_tipo_servicio;
    
    public function __construct($id_tipo_servicio=0, $id_plan=0, $descripcion=""){
        $this->id_tipo_servicio = $id_tipo_servicio;
        $this->id_plan = $id_plan;
        $this->descripcion = $descripcion;
    }

    public function consultarServicioPorIdPlan(){
        return "SELECT t.id_tipo_servicio, tipo_servicio
                FROM plan JOIN tipo_servicio as t ON (plan.id_tipo_servicio = t.id_tipo_servicio) 
                WHERE id_plan = ".$this->id_plan."
                ";
    }
} 
?>