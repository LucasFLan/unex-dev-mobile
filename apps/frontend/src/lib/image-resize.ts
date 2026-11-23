/** Lado maior da imagem depois de reduzida: legível para conferir documento, leve para enviar pelo celular. */
const MAX_SIDE = 1600;

type ResizeOptions = {
  /** Lado maior do resultado, em pixels (padrão 1600). */
  maxSide?: number;
  /** Recorta o centro num quadrado antes de reduzir (foto de perfil). */
  square?: boolean;
};
const JPEG_QUALITY = 0.85;

function loadImage(file: File): Promise<HTMLImageElement> {
  return new Promise((resolve, reject) => {
    const url = URL.createObjectURL(file);
    const image = new Image();
    image.onload = () => {
      URL.revokeObjectURL(url);
      resolve(image);
    };
    image.onerror = () => {
      URL.revokeObjectURL(url);
      reject(new Error("decode"));
    };
    image.src = url;
  });
}

/**
 * Reduz e converte a foto para JPEG no próprio aparelho. Fotos de câmera de celular costumam ter
 * 4–12 MB (às vezes em HEIC); depois disso ficam com algumas centenas de KB.
 * Lança erro se o navegador não conseguir abrir a imagem.
 */
export async function resizeImageForUpload(
  file: File,
  baseName: string,
  { maxSide = MAX_SIDE, square = false }: ResizeOptions = {}
): Promise<File> {
  const image = await loadImage(file); // O navegador já aplica a rotação (EXIF) da foto.
  // Região da foto original que vai para o resultado: inteira, ou o quadrado central.
  const side = Math.min(image.naturalWidth, image.naturalHeight);
  const sourceWidth = square ? side : image.naturalWidth;
  const sourceHeight = square ? side : image.naturalHeight;
  const sourceX = (image.naturalWidth - sourceWidth) / 2;
  const sourceY = (image.naturalHeight - sourceHeight) / 2;

  const scale = Math.min(1, maxSide / Math.max(sourceWidth, sourceHeight));
  const width = Math.max(1, Math.round(sourceWidth * scale));
  const height = Math.max(1, Math.round(sourceHeight * scale));

  const canvas = document.createElement("canvas");
  canvas.width = width;
  canvas.height = height;
  const context = canvas.getContext("2d");
  if (!context) throw new Error("canvas");
  context.fillStyle = "#fff"; // PNG com transparência não fica preto no JPEG.
  context.fillRect(0, 0, width, height);
  context.drawImage(image, sourceX, sourceY, sourceWidth, sourceHeight, 0, 0, width, height);

  const blob = await new Promise<Blob | null>((resolve) =>
    canvas.toBlob(resolve, "image/jpeg", JPEG_QUALITY)
  );
  if (!blob) throw new Error("encode");
  return new File([blob], `${baseName}.jpg`, { type: "image/jpeg" });
}
