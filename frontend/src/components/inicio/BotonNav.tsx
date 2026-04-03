
import { useLocation } from "wouter";

type BotonProps = {
    nombre: string;
    ubicacion: string;
    className?: string;
};

export default function BotonNav(props: BotonProps){
    const [, setLocation] = useLocation();

    return(
        <button className={props.className} onClick={() => setLocation(props.ubicacion)}>
            {props.nombre} 
        </button>
    )
}