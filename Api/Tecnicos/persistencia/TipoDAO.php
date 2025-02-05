<?php 
class TipoDAO{
    private $id_tipo;
    private $descripcion;
    private $icon;

    public function __construct($id_tipo=0, $descripcion="", $icon=""){
        $this->id_tipo = $id_tipo;
        $this->descripcion = $descripcion;
        $this->icon = $icon;
    }

    public function consultar(){
        return "SELECT tipo_ticket, nombre_icono
                FROM tipo_ticket
                WHERE id_tipo_ticket = ".$this->id_tipo."
        ";
    }
}
?>