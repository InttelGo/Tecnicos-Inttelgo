<?php 
class ObservacionDAO{
    private $id_instalacion;
    private $id;
    private $descripcion;

    public function __construct($id_instalacion =0, $id=0, $descripcion=""){
        $this->id_instalacion = $id_instalacion;
        $this->id = $id;
        $this->descripcion = $descripcion;
    }

    public function consultarPorUltimoId(){
        return "SELECT id_obs_instalacion, obs_instalacion 
                FROM obs_instalacion
                WHERE id_instalacion = $this->id_instalacion
                ORDER BY id_obs_instalacion DESC
                LIMIT 1";
    }
}
?>