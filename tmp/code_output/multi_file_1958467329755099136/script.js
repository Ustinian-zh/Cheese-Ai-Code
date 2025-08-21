let tasks = JSON.parse(localStorage.getItem('tasks')) || [];
function renderTasks() {
    document.getElementById('taskList').innerHTML = tasks.map((task, i) => 
        `<li>${task} <span class="delete" onclick="deleteTask(${i})">×</span></li>`
    ).join('');
}
function addTask() {
    const input = document.getElementById('taskInput');
    if (input.value.trim()) {
        tasks.push(input.value.trim());
        input.value = '';
        localStorage.setItem('tasks', JSON.stringify(tasks));
        renderTasks();
    }
}
function deleteTask(index) {
    tasks.splice(index, 1);
    localStorage.setItem('tasks', JSON.stringify(tasks));
    renderTasks();
}
renderTasks();