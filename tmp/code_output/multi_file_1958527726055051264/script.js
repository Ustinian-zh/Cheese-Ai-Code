document.getElementById('task-form').addEventListener('submit', function(e) {
    e.preventDefault();
    const taskInput = document.getElementById('task-input');
    if (taskInput.value.trim()) {
        const li = document.createElement('li');
        li.className = 'task';
        li.innerHTML = `<span>${taskInput.value}</span>
            <div>
                <button onclick="this.parentNode.parentNode.classList.toggle('completed')">完成</button>
                <button onclick="this.parentNode.parentNode.remove()">删除</button>
            </div>`;
        document.getElementById('task-list').appendChild(li);
        taskInput.value = '';
    }
});