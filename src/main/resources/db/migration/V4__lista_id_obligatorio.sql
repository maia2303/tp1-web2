-- crear una lista por defecto si no existe
INSERT INTO listas (nombre)
VALUES ('Sin clasificar')
ON CONFLICT DO NOTHING;
-- actualizar la columna lista_id de favoritos con el id de la lista por defecto
UPDATE favoritos
SET lista_id = (SELECT id FROM listas WHERE nombre = 'Sin clasificar' LIMIT 1)
WHERE lista_id IS NULL;

-- volver la columna obligatoria(not null)
ALTER TABLE favoritos
ALTER COLUMN lista_id SET NOT NULL;