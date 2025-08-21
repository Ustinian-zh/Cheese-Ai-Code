const form = document.getElementById('task-form');
const input = document.getElementById('task-input');
const list = document.getElementById('task-list');

form.addEventListener('submit', e => {
    e.preventDefault();
    const taskText = input.value.trim();
    if (taskText) {
        const li = document.createElement('li');
        li.innerHTML = `
            <span>${taskText}</span>
            <span class="delete">删除</span>
        `;
        li.querySelector('span').addEventListener('click', () => {
            li.querySelector('span').classList.toggle('completed');
        });
        li.querySelector('.delete').addEventListener('click', () => {
            li.remove();
        });
        list.appendChild(li);
        input.value = '';
    }
});