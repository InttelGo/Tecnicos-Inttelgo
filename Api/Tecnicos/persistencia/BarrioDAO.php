<?php 
class BarrioDAO{
    private $id_barrio;
    private $nombre;
    private $prefijo;

    public function __construct($id_barrio=0, $nombre="", $prefijo=""){
        $this->id_barrio = $id_barrio;
        $this->nombre = $nombre;
        $this->prefijo = $prefijo;
    }
    
    public function consultarTodosPrefijos(){
        return "SELECT prefijo 
                FROM barrio
                GROUP BY prefijo
        ";
    }

}
?>