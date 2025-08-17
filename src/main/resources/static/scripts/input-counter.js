document.addEventListener('DOMContentLoaded', () => {
  const input = document.getElementById('input-text');
  const output = document.getElementById('input-count');
  if (!input || !output) return;

  const update = () => { output.textContent = String(input.value.length); };

  update();
  input.addEventListener('input', update);
});
