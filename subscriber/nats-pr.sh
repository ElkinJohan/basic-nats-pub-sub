# Configuración de NATS
STREAM_NAME="mi_stream"
SUBJECTS=("subject1" "subject2" "subject3")
CONSUMER_GROUP="mi_grupo"

# Crear el stream
nats stream add $STREAM_NAME --subjects ${SUBJECTS[@]}

# Crear los consumidores para cada subject
for SUBJECT in "${SUBJECTS[@]}"; do
  nats consumer add $STREAM_NAME $CONSUMER_GROUP --filter $SUBJECT
done

echo "Configuración de NATS completada."