/** Lado maior da imagem depois de reduzida: legível para conferir documento, leve para enviar pelo celular. */
const MAX_SIDE = 1600;
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
export async function resizeImageForUpload(file: File, baseName: string): Promise<File> {
  const image = await loadImage(file); // O navegador já aplica a rotação (EXIF) da foto.
  const scale = Math.min(1, MAX_SIDE / Math.max(image.naturalWidth, image.naturalHeight));
  const width = Math.max(1, Math.round(image.naturalWidth * scale));
  const height = Math.max(1, Math.round(image.naturalHeight * scale));

  const canvas = document.createElement("canvas");
  canvas.width = width;
  canvas.height = height;
  const context = canvas.getContext("2d");
  if (!context) throw new Error("canvas");
  context.fillStyle = "#fff"; // PNG com transparência não fica preto no JPEG.
  context.fillRect(0, 0, width, height);
  context.drawImage(image, 0, 0, width, height);

  const blob = await new Promise<Blob | null>((resolve) =>
    canvas.toBlob(resolve, "image/jpeg", JPEG_QUALITY)
  );
  if (!blob) throw new Error("encode");
  return new File([blob], `${baseName}.jpg`, { type: "image/jpeg" });
}
